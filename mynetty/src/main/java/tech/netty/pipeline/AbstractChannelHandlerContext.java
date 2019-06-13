package tech.netty.pipeline;

import java.net.SocketAddress;

/**
 * @author leexuehan on 2019/6/5.
 */
public abstract class AbstractChannelHandlerContext implements ChannelHandlerContext {

    volatile AbstractChannelHandlerContext next;
    volatile AbstractChannelHandlerContext prev;

    private final String name;
    private final DefaultChannelPipeline pipeline;

    private final boolean inBound;
    private final boolean outBound;

    public AbstractChannelHandlerContext(DefaultChannelPipeline pipeline, Class<? extends ChannelHandler> handlerClass, String name) {
        this.pipeline = pipeline;
        this.inBound = ChannelInboundHandler.class.isAssignableFrom(handlerClass);
        this.outBound = ChannelOutboundHandler.class.isAssignableFrom(handlerClass);
        this.name = name;
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
        AbstractChannelHandlerContext next = findContextOutbound();
        next.invokeConnect(remoteAddress);
    }

    private void invokeConnect(SocketAddress remoteAddress) {
        try {
            ((ChannelOutboundHandler) handler()).connect(this, remoteAddress);
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println("connect exception");
        }
    }


    @Override
    public ChannelHandlerContext fireChannelRead(Object msg) {
        AbstractChannelHandlerContext next = findContextInbound();
        next.invokeChannelRead(msg);
        return this;
    }

    private void invokeChannelRead(Object msg) {
        ((ChannelInboundHandler) handler()).channelRead(this, msg);
    }

    @Override
    public AbstractChannelHandlerContext fireChannelRegistered() {
        return null;
    }

    private AbstractChannelHandlerContext findContextOutbound() {
        AbstractChannelHandlerContext ctx = this;
        do {
            ctx = ctx.prev;
        } while (ctx.isInBound() && ctx.prev != null);

        return ctx;
    }

    private AbstractChannelHandlerContext findContextInbound() {
        AbstractChannelHandlerContext ctx = this;
        do {
            ctx = ctx.next;
        } while (ctx.isOutBound());
        return ctx;
    }
}
