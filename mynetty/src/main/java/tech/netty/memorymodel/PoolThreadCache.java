package tech.netty.memorymodel;

/**
 * @author leexuehan on 2019/6/24.
 */

import tech.netty.common.buffer.PooledByteBuf;
import tech.netty.memorymodel.PoolArena.SizeClass;

import java.nio.ByteBuffer;

/***
 * 每个线程的缓存
 */
public class PoolThreadCache {
    //堆上分配
    final PoolArena<byte[]> heapArea;
    //直接分配
    final PoolArena<ByteBuffer> directArena;

    private final MemoryRegionCache<byte[]>[] tinySubPageHeapCaches;
    private final MemoryRegionCache<byte[]>[] smallSubPageHeapCaches;
    private final MemoryRegionCache<byte[]>[] normalHeapCaches;


    public PoolThreadCache(PoolArena<byte[]> heapArea, PoolArena<ByteBuffer> directArena,
                           int tinyCacheSize, int smallCacheSize, int normalCacheSize,
                           int maxCachedBufferCapacity, int freeSweepAllocationThreshold) {
        this.heapArea = heapArea;
        this.directArena = directArena;
        if (directArena != null) {

        }

        if (heapArea != null) {
            tinySubPageHeapCaches = createSubPageCaches(tinyCacheSize, PoolArena.numOfTinySubpagePools, SizeClass.Tiny);
            smallSubPageHeapCaches = createSubPageCaches(smallCacheSize, heapArea.numOfSmallSubpagePools, SizeClass.Small);
            normalHeapCaches = createNormalCaches(normalCacheSize, maxCachedBufferCapacity, heapArea);
            heapArea.numThreadCaches.getAndIncrement();
        }
        //没有配置堆内存
        else {
            tinySubPageHeapCaches = null;
            smallSubPageHeapCaches = null;
            normalHeapCaches = null;
        }
    }

    private MemoryRegionCache<byte[]>[] createNormalCaches(int cacheSize, int maxCachedBufferCapacity, PoolArena<byte[]> area) {
        if (cacheSize > 0 && maxCachedBufferCapacity > 0) {
            int max = Math.min(area.chunkSize, maxCachedBufferCapacity);
            int arraySize = Math.max(1, log2(max / area.pageSize) + 1);

            MemoryRegionCache[] caches = new MemoryRegionCache[arraySize];
            for (int i = 0; i < caches.length; i++) {
                caches[i] = new NormalMemoryRegionCache(cacheSize);
            }
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> MemoryRegionCache<T>[] createSubPageCaches(int cacheSize, int numCaches, SizeClass sizeClass) {
        if (cacheSize > 0 && numCaches > 0) {
            MemoryRegionCache<T>[] cache = new MemoryRegionCache[numCaches];
            for (int i = 0; i < cache.length; i++) {
                cache[i] = new SubPageMemoryRegionCache<>(cacheSize, sizeClass);
            }
            return cache;
        } else {
            return null;
        }
    }

    public <T> boolean allocateNormal(PoolArena tArena, PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {

    }

    public <T> boolean allocateSmall(PoolArena tArena, PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {


    }

    public <T> boolean allocateTiny(PoolArena tArena, PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {
        int tinyIdx = PoolArena.tinyIdx(normCapacity);


    }

    private static class SubPageMemoryRegionCache<T> extends MemoryRegionCache<T> {
        public SubPageMemoryRegionCache(int cacheSize, SizeClass sizeClass) {
            super(cacheSize, sizeClass);
        }

    }

    private class NormalMemoryRegionCache extends MemoryRegionCache {
        NormalMemoryRegionCache(int cacheSize) {
            super(cacheSize, SizeClass.Normal);
        }
    }
}
