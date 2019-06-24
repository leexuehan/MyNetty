package tech.netty.memorymodel;

/**
 * @author leexuehan on 2019/6/24.
 */
final class ChunkList<T> {
    private final Arena<T> arena;

    private ChunkList<T> nextList;
    private ChunkList<T> prevList;
    private Chunk<T> head;

    private final int minUsage;
    private final int maxUsage;
    private final int maxCapacity;


    public ChunkList(Arena<T> arena, ChunkList<T> nextList, int minUsage, int maxUsage, int chunkSize) {
        this.arena = arena;
        this.nextList = nextList;
        this.minUsage = minUsage;
        this.maxUsage = maxUsage;
        maxCapacity = calculateMaxCapacity(minUsage, chunkSize);
    }

    private int calculateMaxCapacity(int minUsage, int chunkSize) {

        return 0;
    }

    void prevList(ChunkList<T> prevList) {
        this.prevList = prevList;
    }
}
