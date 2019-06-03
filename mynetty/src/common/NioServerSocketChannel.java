package common;

import threadmodel.NioEventLoop;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;

public class NioServerSocketChannel {
    private NioEventLoop eventLoop;
    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();

    //java nio channel
    private ServerSocketChannel ch;

    private final int readInterestOps;

    public NioServerSocketChannel() {
        SelectorProvider provider = DEFAULT_SELECTOR_PROVIDER;
        ch = newChannel(provider);
        try {
            ch.configureBlocking(false);
        } catch (IOException e) {
            throw new ChannelException("failed to enter non-blocking mode", e);
        }
        this.readInterestOps = SelectionKey.OP_ACCEPT;

    }

    private ServerSocketChannel newChannel(SelectorProvider provider) {
        try {
            return provider.openServerSocketChannel();
        } catch (IOException e) {
            throw new ChannelException("open server socket channel exception");
        }
    }

    public void bind(final SocketAddress socketAddress) {

    }

    public void setEventLoop(NioEventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }

    public NioEventLoop getEventLoop() {
        return eventLoop;
    }
}
