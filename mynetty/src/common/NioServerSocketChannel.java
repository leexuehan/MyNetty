package common;

import threadmodel.NioEventLoop;

import java.net.SocketAddress;

public class NioServerSocketChannel {
    private NioEventLoop eventLoop;

    public void bind(final SocketAddress socketAddress) {

    }

    public void setEventLoop(NioEventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }
}
