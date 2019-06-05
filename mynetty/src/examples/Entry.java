package examples;

import pipeline.DefaultChannelPipeline;

import java.net.InetSocketAddress;

/**
 * the entry point of all examples
 */
public class Entry {
    public static void main(String[] args) {
//        testReactor();
        testPipeline();
    }

    private static void testPipeline() {
        DefaultChannelPipeline pipeline = new DefaultChannelPipeline();
        pipeline.connect(new InetSocketAddress(6666));
    }


    private static void testReactor() {
        Reactor reactor = new Reactor(9999);
        new Thread(reactor).start();
    }
}
