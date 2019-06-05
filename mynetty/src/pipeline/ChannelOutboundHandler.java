package pipeline;

import java.net.SocketAddress;

/**
 * @author leexuehan on 2019/6/5.
 */
public interface ChannelOutboundHandler extends ChannelHandler {
    void connect() throws Exception;

    void bind(ChannelHandlerContext ctx, SocketAddress localAddress) throws Exception;
}
