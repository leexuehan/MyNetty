package tech.netty.memorymodel;


import tech.netty.memorymodel.PoolArena.SizeClass;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author leexuehan on 2019/6/25.
 */
public abstract class MemoryRegionCache<T> {
    private final int size;
    private final Queue<Entry<T>> queue;
    private final SizeClass sizeClass;
    private int allocations;


    MemoryRegionCache(int size, SizeClass sizeClass) {
        this.size = MathUtil.safeFindNextPositivePowerOfTwo(size);
        //todo 暂时以一个普通的队列代替
        queue = new ArrayDeque<>(size);
        this.sizeClass = sizeClass;

    }

    static final class Entry<T> {

    }
}
