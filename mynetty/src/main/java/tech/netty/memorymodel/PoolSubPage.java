package tech.netty.memorymodel;

/**
 * @author leexuehan on 2019/6/24.
 */

/**
 * page的下属分配单元
 */
final class PoolSubPage<T> {
    PoolChunk<T> chunk; //所属的 Chunk

    private final int memoryMapIdx;
    private final long[] bitmap;
    private final int runOffset;
    private final int pageSize;

    private boolean doNotDestory;
    PoolSubPage<T> prev;

    PoolSubPage<T> next;
    int elemSize;
    private int numAvail;
    private int nextAvail;
    private int bitmapLength;
    private int maxNumElems;


    PoolSubPage(int pageSize) {

    }

    public long allocate() {
        if (elemSize == 0) {
            return toHandle(0);
        }

        if (numAvail == 0 || !doNotDestory) {
            return -1;
        }

        final int bitmapIdx = getNextAvail();
        int q = bitmapIdx >>> 6;
        int r = bitmapIdx & 63;
        bitmap[q] = 1L << r;

        if (--numAvail == 0) {
            removeFromPool();
        }

        return toHandle(bitmapIdx);
    }

    private void removeFromPool() {

    }

    private long toHandle(int bitMapIdx) {
        return 0x4000000000000000L | (long) bitMapIdx << 32 | memoryMapIdx;
    }

    private int getNextAvail() {
        int nextAvail = this.nextAvail;
        if (nextAvail >= 0) {
            this.nextAvail = -1;
            return nextAvail;
        }
        return findNextAvail();
    }

    private int findNextAvail() {
        final long[] bitmap = this.bitmap;
        final int bitmapLength = this.bitmapLength;
        for (int i = 0; i < bitmapLength; i++) {
            long bits = bitmap[i];
            if (~bits != 0) {
                return findNextAvail0(i, bits);
            }
        }
        return -1;
    }

    private int findNextAvail0(int i, long bits) {
        final int maxNumElems = this.maxNumElems;
        final int baseVal = i << 6;

        for (int j = 0; j < 64; j++) {
            if ((bits & 1) == 0) {
                int val = baseVal | j;
                if (val < maxNumElems) {
                    return val;
                } else {
                    break;
                }
            }
            bits >>>= 1;
        }
        return -1;
    }
}
