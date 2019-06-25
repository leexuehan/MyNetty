package tech.netty.memorymodel;

/**
 * @author leexuehan on 2019/6/24.
 */

import tech.netty.common.buffer.PooledByteBuf;

import java.nio.ByteBuffer;

/**
 * 次于 PoolArena 的内存单元
 * 管理 Page 的分配
 */
public class PoolChunk<T> {
    void initBufWithSubpage(PooledByteBuf<T> buf, ByteBuffer nioBuffer, long handle, int reqCapacity) {
        initBufWithSubpage(buf, nioBuffer, handle, bitmapIdx(handle), reqCapacity);
    }

    private void initBufWithSubpage(PooledByteBuf<T> buf, ByteBuffer nioBuffer, long handle, int bitmapIdx, int reqCapacity) {

    }

    private int bitmapIdx(long handle) {
        int memoryMapIdx = memoryMapIdx(handle);

    }

    private int memoryMapIdx(long handle) {
        return (int) (handle >>> Integer.SIZE);
    }
}
