package tech.netty.memorymodel;

/**
 * @author leexuehan on 2019/6/24.
 */


import tech.netty.common.buffer.PooledByteBuf;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Netty 全局内存池
 * <p>
 * ---------------
 * |   PoolArena      |
 * ---------------
 * |                            \          \
 * |                             \          \
 * |                              \          \
 * ------
 * |Chunk1|                       Chunk2  ... ChunkN
 * --------
 * |            \           \
 * |             \           \
 * |              \           \
 * Page1        Page2 .... Page2048
 * |        \
 * |         \
 * |          \
 * SubPage1   SubPage2...
 */
public class PoolArena<T> {

    //大小种类
    enum SizeClass {
        Tiny,
        Small,
        Normal
    }

    final int pageSize;
    final int pageShifts;
    final int chunkSize;
    /**
     * 以下两变量实现：以此数值进行对齐，i.e:
     * 如果为 64，则归一化后的容量皆以 64 为单位进行对齐
     */
    final int directMemoryCacheAlignment;
    final int directMemoryCacheAlignmentMask;

    final int numOfSmallSubpagePools;
    static final int numOfTinySubpagePools = 512 >>> 4; //32
    final int subpageOverflowMask;

    //使用率不同的 PoolChunk 链表
    private final PoolChunkList<T> q000;
    private final PoolChunkList<T> q025;
    private final PoolChunkList<T> q050;
    private final PoolChunkList<T> q075;
    private final PoolChunkList<T> q100;
    private final PoolChunkList<T> qInit;

    private final PoolSubPage<T>[] tinySubpages;
    private final PoolSubPage<T>[] smallSubpages;

    //Arena 内存块里面的 ThreadCache 数量
    final AtomicInteger numThreadCaches = new AtomicInteger();


    public PoolArena(int pageSize, int pageShifts, int chunkSize, int cacheAlignment) {
        this.pageSize = pageSize;
        this.pageShifts = pageShifts;
        this.chunkSize = chunkSize;
        directMemoryCacheAlignment = cacheAlignment;
        directMemoryCacheAlignmentMask = cacheAlignment - 1;

        tinySubpages = newSubpageArray(numOfTinySubpagePools);
        for (int i = 0; i < tinySubpages.length; i++) {
            tinySubpages[i] = newSubpageHead(pageSize);
        }

        numOfSmallSubpagePools = pageShifts - 9;
        smallSubpages = newSubpageArray(numOfSmallSubpagePools);
        for (int i = 0; i < smallSubpages.length; i++) {
            smallSubpages[i] = newSubpageHead(pageSize);
        }

        q100 = new PoolChunkList<>(this, null, 100, Integer.MAX_VALUE, chunkSize);
        q075 = new PoolChunkList<>(this, q100, 75, 100, chunkSize);
        q050 = new PoolChunkList<>(this, q075, 50, 75, chunkSize);
        q025 = new PoolChunkList<>(this, q075, 25, 50, chunkSize);
        q000 = new PoolChunkList<>(this, q025, 1, 25, chunkSize);
        qInit = new PoolChunkList<>(this, q000, Integer.MIN_VALUE, 25, chunkSize);

        q100.prevList(q075);
        q075.prevList(q050);
        q050.prevList(q025);
        q025.prevList(q000);
        q000.prevList(null);
        qInit.prevList(qInit);

        subpageOverflowMask = ~(pageSize - 1);
    }

    @SuppressWarnings("unchecked")
    private PoolSubPage<T>[] newSubpageArray(int size) {
        return new PoolSubPage[size];
    }

    private PoolSubPage<T> newSubpageHead(int pageSize) {
        PoolSubPage<T> head = new PoolSubPage<>(pageSize);
        head.prev = head;
        head.next = head;
        return head;
    }

    //capacity < pageSize
    boolean isTinyOrSmall(int normCapacity) {
        return (normCapacity & subpageOverflowMask) == 0;
    }

    // < 512
    static boolean isTiny(int normCapacity) {
        return (normCapacity & 0xFFFFFE00) == 0;
    }

    int normalizeCapacity(int reqCapacity) {
        if (reqCapacity < 0) {
            throw new IllegalArgumentException("reqCapacity:" + reqCapacity + "(expected: >= 0)");
        }

        if (reqCapacity >= chunkSize) {
            return directMemoryCacheAlignment == 0 ? reqCapacity : alignCapacity(reqCapacity);
        }

        //大于 512 的为 small，通过移位操作搞定
        // 要保证按照下面的次序进行归一化: 512(2^9)、1024(2^10)、2048(2^11)、4096(2^12).....
        if (!isTiny(reqCapacity)) { // >= 512
            int normalizedCapacity = reqCapacity;
            normalizedCapacity--;
            normalizedCapacity |= normalizedCapacity >>> 1;
            normalizedCapacity |= normalizedCapacity >>> 2;
            normalizedCapacity |= normalizedCapacity >>> 4;
            normalizedCapacity |= normalizedCapacity >>> 8;
            normalizedCapacity |= normalizedCapacity >>> 16;
            //因为int类型为 32 位，所以到了此处可以保证32位全1，加1进位可以保证是取整倍数
            normalizedCapacity++;

            if (normalizedCapacity < 0) {
                normalizedCapacity >>>= 1;
            }

            return normalizedCapacity;
        }

        // < 512 的为 tiny 单独考虑归一化，只需要保证是 16 的倍数即可
        if (directMemoryCacheAlignment > 0) {
            return alignCapacity(reqCapacity);
        }

        //后4位为0，意即16的倍数，无须再继续归一化
        if ((reqCapacity & 15) == 0) {
            return reqCapacity;
        }

        return (reqCapacity & ~15) + 16;
    }

    int alignCapacity(int reqCapacity) {
        int delta = reqCapacity & directMemoryCacheAlignmentMask;
        //如果 delta 不为 0，说明该数有零头，需要在下一步减去后，加上对齐位即可
        return delta == 0 ? reqCapacity : reqCapacity + directMemoryCacheAlignment - delta;
    }

    PooledByteBuf<T> allocate(PoolThreadCache cache, int reqCapacity, int maxCapacity) {
        PooledByteBuf<T> buf = newByteBuf(maxCapacity);
        allocate(cache, buf, reqCapacity);
        return buf;
    }

    //核心分配方法
    private void allocate(PoolThreadCache cache, PooledByteBuf<T> buf, int reqCapacity) {
        final int normCapacity = normalizeCapacity(reqCapacity);
        // 分配 normal 或者 small
        if (isTinyOrSmall(normCapacity)) {
            int tableIdx;
            PoolSubPage<T>[] table;
            boolean isTiny = isTiny(normCapacity);
            //tiny
            if (isTiny) {
                if (cache.allocateTiny(this, buf, reqCapacity, normCapacity)) {
                    return;
                }
                tableIdx = tinyIdx(normCapacity);
                table = tinySubpages;
            }
            //small
            else {
                if (cache.allocateSmall(this, buf, reqCapacity, normCapacity)) {
                    return;
                }
                tableIdx = smallIdx(normCapacity);
                table = smallSubpages;
            }
            final PoolSubPage<T> head = table[tableIdx];
            synchronized (head) {
                final PoolSubPage<T> s = head.next;
                if (s != head) {
                    long handle = s.allocate();
                    s.chunk.initBufWithSubpage(buf, null, handle, reqCapacity);
                    return;
                }
            }

            synchronized (this) {
                allocateNormal(buf, reqCapacity, normCapacity);
            }
        }
        // 分配 normal
        if (normCapacity <= chunkSize) {
            //如果能在线程本地的缓存中分配，则分配成功后返回
            if (cache.allocateNormal(this, buf, reqCapacity, normCapacity)) {
                return;
            }
            synchronized (this) {
                allocateNormal(buf, reqCapacity, normCapacity);
            }
        }
        //分配 huge
        else {
            //huge 在线程本地没有缓存
            allocateHuge(buf, reqCapacity);
        }

    }

    //归一化后 tiny 的 normCapacity 为 16 的倍数，所以求其下标只向右移4位即可。
    static int tinyIdx(int normCapacity) {
        return normCapacity >>> 4;
    }

    //归一化后 small 的 normCapacity 为 2 的指数次,起始为：2^9,也就是在第 10 位上不为0
    static int smallIdx(int normCapacity) {
        int tableIdx = 0;
        int i = normCapacity >>> 10;
        while (i != 0) {
            i >>>= 1;
            tableIdx++;
        }
        return tableIdx;
    }

    private void allocateNormal(PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {

    }

    private void allocateHuge(PooledByteBuf<T> buf, int reqCapacity) {

    }

    private PooledByteBuf<T> newByteBuf(int maxCapacity) {
        return null;
    }

}
