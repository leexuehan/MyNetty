package pipeline;

import java.net.SocketAddress;

/**
 * @author leexuehan on 2019/6/5.
 */
public class DefaultChannelPipeline implements ChannelPipeline {
    final HeadContext head;
    final TailContext tail;

    public DefaultChannelPipeline() {
        head = new HeadContext(this);
        tail = new TailContext(this);

        head.next = tail;
        tail.prev = head;
    }


    @Override
    public ChannelPipeline addLast(String name, ChannelHandler handler) {
        return null;
    }

    public void connect(SocketAddress remoteAddress) {
        tail.connect(remoteAddress);
    }

    public void fireChannelRead(Object msg) {
        head.fireChannelRead(msg);
    }

    final class HeadContext extends DefaultChannelHandlerContext implements ChannelOutboundHandler, ChannelInboundHandler {

        HeadContext(DefaultChannelPipeline pipeline) {
            super(pipeline, null, true, true);
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {

        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {

        }

        @Override
        public void connect() throws Exception {
            //最终是由head节点真正处理连接事件
            System.out.println("do actual connection event here in head node!");
        }

        @Override
        public void bind(ChannelHandlerContext ctx, SocketAddress localAddress) throws Exception {
            //最终是由head节点真正处理绑定事件
            System.out.println("do actual bind event here in head node!");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            System.out.println("begin to execute read event here in head node!");
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        }

        @Override
        public ChannelHandler handler() {
            return this;
        }
    }

    final class TailContext extends DefaultChannelHandlerContext implements ChannelInboundHandler {

        TailContext(DefaultChannelPipeline pipeline) {
            super(pipeline, null, true, false);
        }


        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {
            //do nothing

        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            //do nothing
        }
        
        @Override
        public ChannelHandler handler() {
            return this;
        }


        //到达流水线的尾端，直接丢弃
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            System.out.println("msg reached the handler tail,so discard it directly");
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            //do nothing
        }
    }
}
