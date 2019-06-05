package pipeline;

import java.net.SocketAddress;

/**
 * @author leexuehan on 2019/6/5.
 */
public class DefaultChannelHandlerContext implements ChannelHandlerContext {
    volatile DefaultChannelHandlerContext next;
    volatile DefaultChannelHandlerContext prev;

    private final DefaultChannelPipeline pipeline;

    private final boolean inBound;
    private final boolean outBound;

    public DefaultChannelHandlerContext(DefaultChannelPipeline pipeline, boolean inBound, boolean outBound) {
        this.pipeline = pipeline;
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
}
