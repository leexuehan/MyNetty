package tech.netty.pipeline;

/**
 * @author leexuehan on 2019/6/5.
 */
public interface ChannelHandler {
    void handlerAdded(ChannelHandlerContext ctx);

    void handlerRemoved(ChannelHandlerContext ctx);
}
