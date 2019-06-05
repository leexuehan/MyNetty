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
            super(pipeline, true, true);
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {

        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {

        }

        @Override
        public void connect() throws Exception {

        }

        @Override
        public void bind(ChannelHandlerContext ctx, SocketAddress localAddress) throws Exception {

        }
    }

    final class TailContext extends DefaultChannelHandlerContext implements ChannelInboundHandler {

        TailContext(DefaultChannelPipeline pipeline) {
            super(pipeline, true, false);
        }


        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {

        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {

        }

        @Override
        public void bind(SocketAddress localAddress) {

        }

        @Override
        public void connect(SocketAddress remoteAddress) {
            final ChannelHandlerContext next = findContextOutbound();
            next.connect(remoteAddress);
        }

        @Override
        public ChannelHandlerContext fireChannelRead(Object msg) {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelRegistered() {
            return null;
        }

        private ChannelHandlerContext findContextOutbound() {
            DefaultChannelHandlerContext ctx = this;
            do {
                ctx = ctx.prev;
            } while (ctx.isInBound());

            return ctx;
        }

        private ChannelHandlerContext findContextInbound() {
            DefaultChannelHandlerContext ctx = this;
            do {
                ctx = ctx.next;
            } while (ctx.isOutBound());
            return ctx;
        }
    }
}
