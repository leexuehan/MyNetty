package tech.netty.memorymodel;

/**
 * @author leexuehan on 2019/6/24.
 */


/**
 * Netty 全局内存池
 * <p>
 * ---------------
 * |   Arena      |
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
 * |     \
 * |      \
 * |       \
 * SubPage1 SubPage2...
 */
public class Arena<T> {

    //大小种类
    enum SizeClass {
        Tiny,
        Small,
        Normal
    }

    final int numOfSmallSubpages;
    static final int numOfTinySubpages = 512 >>> 4; //32

    //使用率不同的 Chunk 链表
    private final ChunkList<T> q000;
    private final ChunkList<T> q025;
    private final ChunkList<T> q050;
    private final ChunkList<T> q075;
    private final ChunkList<T> q100;
    private final ChunkList<T> qInit;

    private final SubPage<T>[] tinySubpages;
    private final SubPage<T>[] smallSubpages;

    public Arena(int pageSize, int pageShifts, int chunkSize) {
        tinySubpages = new SubPage[numOfTinySubpages];
        for (int i = 0; i < tinySubpages.length; i++) {
            tinySubpages[i] = newSubpageHead(pageSize);
        }

        numOfSmallSubpages = pageShifts - 9;
        smallSubpages = new SubPage[numOfSmallSubpages];
        for (int i = 0; i < smallSubpages.length; i++) {
            smallSubpages[i] = newSubpageHead(pageSize);
        }

        q100 = new ChunkList<>(this, null, 100, Integer.MAX_VALUE, chunkSize);
        q075 = new ChunkList<>(this, q100, 75, 100, chunkSize);
        q050 = new ChunkList<>(this, q075, 50, 75, chunkSize);
        q025 = new ChunkList<>(this, q075, 25, 50, chunkSize);
        q000 = new ChunkList<>(this, q025, 1, 25, chunkSize);
        qInit = new ChunkList<>(this, q000, Integer.MIN_VALUE, 25, chunkSize);

        q100.prevList(q075);
        q075.prevList(q050);
        q050.prevList(q025);
        q025.prevList(q000);
        q000.prevList(null);
        qInit.prevList(qInit);


    }

    private SubPage<T> newSubpageHead(int pageSize) {
        SubPage<T> head = new SubPage<>(pageSize);
        head.prev = head;
        head.next = head;
        return head;
    }


}
