package tech.netty.memorymodel;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

/**
 * @author leexuehan on 2019/6/24.
 */
public class ArenaTest {
    @Test
    public void testSubpageOverflowMask() throws Exception {

    }


    @Test
    public void testNormalizeCapacity() throws Exception {
        PoolArena<ByteBuffer> arena = new PoolArena<>(0, 9, 99999, 0);
        int[] reqCapacities = {0, 15, 510, 513, 1024, 1023, 1025};
        int[] expectedCapacities = {0, 16, 512, 1024, 1024, 1024, 2048};
        for (int i = 0; i < reqCapacities.length; i++) {
            int result = arena.normalizeCapacity(reqCapacities[i]);
            assertEquals(expectedCapacities[i], result);
        }
    }

    @Test
    public void testNormalizeAlignedCapacity() throws Exception {
        PoolArena<ByteBuffer> arena = new PoolArena<>(0, 9, 99999, 64);
        int[] reqCapacities = {0, 15, 510, 1024, 1023, 1025};
        int[] expectedCapacities = {0, 64, 512, 1024, 1024, 2048};
        for (int i = 0; i < reqCapacities.length; i++) {
            int result = arena.normalizeCapacity(reqCapacities[i]);
            assertEquals(expectedCapacities[i], result);
        }
    }
}