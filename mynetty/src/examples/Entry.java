package examples;

import pipeline.ChannelHandlerContext;
import pipeline.ChannelInboundHandler;
import pipeline.ChannelOutboundHandler;
import pipeline.DefaultChannelPipeline;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

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
//        pipeline.connect(new InetSocketAddress(6666));
        pipeline.addLast("inbound1", new ChannelInboundHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                System.out.println("read msg:[" + msg + "] in handler [inbound1]");
                ctx.fireChannelRead(msg);
            }

            @Override
            public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

            }

            @Override
            public void handlerAdded(ChannelHandlerContext ctx) {

            }

            @Override
            public void handlerRemoved(ChannelHandlerContext ctx) {

            }
        });

        pipeline.addLast("outbound1", new ChannelOutboundHandler() {
            @Override
            public void connect() throws Exception {
                System.out.println("connect event in handler outbound1");
            }

            @Override
            public void bind(ChannelHandlerContext ctx, SocketAddress localAddress) throws Exception {

            }

            @Override
            public void handlerAdded(ChannelHandlerContext ctx) {

            }

            @Override
            public void handlerRemoved(ChannelHandlerContext ctx) {

            }
        });

        pipeline.addLast("inbound2", new ChannelInboundHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                System.out.println("read msg:[" + msg + "] in handler [inbound2]");
                ctx.fireChannelRead(msg);
            }

            @Override
            public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

            }

            @Override
            public void handlerAdded(ChannelHandlerContext ctx) {

            }

            @Override
            public void handlerRemoved(ChannelHandlerContext ctx) {

            }
        });
        pipeline.invokeChannelRead("read msg generated!");
    }


    private static void testReactor() {
        Reactor reactor = new Reactor(9999);
        new Thread(reactor).start();
    }
}
