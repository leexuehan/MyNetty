package tech.netty.pipeline;

import java.net.SocketAddress;

/**
 * @author leexuehan on 2019/6/5.
 */
public interface ChannelOutboundHandler extends ChannelHandler {
    void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress) throws Exception;

    void bind(ChannelHandlerContext ctx, SocketAddress localAddress) throws Exception;
}
