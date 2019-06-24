package tech.netty.memorymodel;

/**
 * @author leexuehan on 2019/6/24.
 */

import tech.netty.common.buffer.PooledByteBuf;

import java.nio.ByteBuffer;

/***
 * 每个线程的缓存
 */
public class PoolThreadCache {
    //堆上分配
    final Arena<byte[]> heapArea;
    //直接分配
    final Arena<ByteBuffer> directArena;

    public PoolThreadCache(Arena<byte[]> heapArea, Arena<ByteBuffer> directArena) {
        this.heapArea = heapArea;
        this.directArena = directArena;
    }

    public <T> boolean allocateNormal(Arena tArena, PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {

    }

    public <T> boolean allocateSmall(Arena tArena, PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {


    }

    public <T> boolean allocateTiny(Arena tArena, PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {


    }
}
