import common.NioServerSocketChannel;
import threadmodel.NioEventLoop;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        NioServerSocketChannel serverSocketChannel = new NioServerSocketChannel();
        serverSocketChannel.setEventLoop(new NioEventLoop(10));
        serverSocketChannel.bind(new InetSocketAddress(9999));
    }
}
