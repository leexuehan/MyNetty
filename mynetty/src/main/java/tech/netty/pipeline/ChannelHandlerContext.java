package tech.netty.pipeline;

import java.net.SocketAddress;

/**
 * @author leexuehan on 2019/6/5.
 */
public interface ChannelHandlerContext {

    //出站事件
    void bind(SocketAddress localAddress);

    void connect(SocketAddress remoteAddress);


    //入站事件
    ChannelHandlerContext fireChannelRead(Object msg);

    ChannelHandlerContext fireChannelRegistered();

    ChannelHandler handler();
}
