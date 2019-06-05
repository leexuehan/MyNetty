package pipeline;

import java.net.SocketAddress;

/**
 * @author leexuehan on 2019/6/5.
 */
public class DefaultChannelHandlerContext implements ChannelHandlerContext {
    private final ChannelHandler handler;

    volatile DefaultChannelHandlerContext next;
    volatile DefaultChannelHandlerContext prev;

    private final DefaultChannelPipeline pipeline;

    private final boolean inBound;
    private final boolean outBound;

    public DefaultChannelHandlerContext(DefaultChannelPipeline pipeline, ChannelHandler channelHandler,
                                        boolean inBound, boolean outBound) {
        this.pipeline = pipeline;
        this.handler = channelHandler;
        this.inBound = inBound;
        this.outBound = outBound;
    }

    public DefaultChannelPipeline getPipeline() {
        return pipeline;
    }

    public boolean isInBound() {
        return inBound;
    }

    public boolean isOutBound() {
        return outBound;
    }

    @Override
    public void bind(SocketAddress localAddress) {

    }

    @Override
    public void connect(SocketAddress remoteAddress) {

    }


    @Override
    public ChannelHandlerContext fireChannelRead(Object msg) {

        getPipeline().fireChannelRead(msg);
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelRegistered() {
        return null;
    }

    @Override
    public ChannelHandler handler() {
        return handler;
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
