package tech.netty.memorymodel;

/**
 * @author leexuehan on 2019/6/24.
 */
final class PoolChunkList<T> {
    private final PoolArena<T> arena;

    private PoolChunkList<T> nextList;
    private PoolChunkList<T> prevList;
    private PoolChunk<T> head;

    private final int minUsage;
    private final int maxUsage;
    private final int maxCapacity;


    public PoolChunkList(PoolArena<T> arena, PoolChunkList<T> nextList, int minUsage, int maxUsage, int chunkSize) {
        this.arena = arena;
        this.nextList = nextList;
        this.minUsage = minUsage;
        this.maxUsage = maxUsage;
        maxCapacity = calculateMaxCapacity(minUsage, chunkSize);
    }

    private int calculateMaxCapacity(int minUsage, int chunkSize) {

        return 0;
    }

    void prevList(PoolChunkList<T> prevList) {
        this.prevList = prevList;
    }
}
