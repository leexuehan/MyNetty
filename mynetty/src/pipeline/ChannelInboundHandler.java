package pipeline;

/**
 * @author leexuehan on 2019/6/5.
 */
public interface ChannelInboundHandler extends ChannelHandler {
    void channelRead(ChannelHandlerContext ctx, Object msg);

    void channelRegistered(ChannelHandlerContext ctx) throws Exception;
}
