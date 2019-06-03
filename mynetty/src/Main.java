import common.NioServerSocketChannel;
import threadmodel.NioEventLoop;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        NioServerSocketChannel serverSocketChannel = new NioServerSocketChannel();
        serverSocketChannel.setEventLoop(new NioEventLoop(10));
        serverSocketChannel.bind(new InetSocketAddress(9999));
    }
}
