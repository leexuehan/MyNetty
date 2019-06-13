package tech.netty.examples;


import tech.netty.ChannelException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * an example according to Doug Lea's <>Scala Nio</>
 */
public class Reactor implements Runnable {
    private final Selector selector;
    private final ServerSocketChannel serverSocket;

    public Reactor(int port) {
        selector = openSelector();
        serverSocket = openChannel();
        bind(port);
        configureChannel();
        SelectionKey selectionKey = registerChannel();
        selectionKey.attach(new Acceptor());
    }

    private SelectionKey registerChannel() {
        try {
            return serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            throw new ChannelException("register channel exception", e);
        }
    }

    private void configureChannel() {
        try {
            serverSocket.configureBlocking(false);
        } catch (IOException e) {
            throw new ChannelException("enter into non-block mode failed", e);
        }
    }

    private void bind(int port) {
        try {
            serverSocket.socket().bind(new InetSocketAddress(port));
        } catch (IOException e) {
            throw new ChannelException("bind port failed", e);
        }
    }

    private ServerSocketChannel openChannel() {
        try {
            return ServerSocketChannel.open();
        } catch (IOException e) {
            throw new ChannelException("open server socket channel failed", e);
        }
    }

    private Selector openSelector() {
        try {
            return Selector.open();
        } catch (IOException e) {
            throw new ChannelException("open selector failed");
        }
    }

    //keep selecting and dispatch events
    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    dispatch(iterator.next());
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(SelectionKey key) {
        //get acceptor thread and let it handle the event
        Runnable r = (Runnable) key.attachment();
        if (r != null) {
            r.run();
        }
    }

    class Acceptor implements Runnable {
        @Override
        public void run() {
            try {
                SocketChannel socketChannel = serverSocket.accept();
                if (socketChannel != null) {
                    new Handler(selector, socketChannel);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    final class Handler implements Runnable {
        private static final int MAXIN = 1024;
        private static final int MAXOUT = 1024;

        final SocketChannel socketChannel;
        final SelectionKey selectionKey;

        ByteBuffer input = ByteBuffer.allocate(MAXIN);
        ByteBuffer output = ByteBuffer.allocate(MAXOUT);

        static final int READING = 0;
        static final int SENDING = 1;

        int state = READING;

        Handler(Selector selector, SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
            selectionKey = initAndRegister(selector, socketChannel);
            selectionKey.attach(this);
            selectionKey.interestOps(SelectionKey.OP_READ);
            selector.wakeup();
        }

        private SelectionKey initAndRegister(Selector selector, SocketChannel socketChannel) {
            try {
                socketChannel.configureBlocking(false);
                SelectionKey sk = socketChannel.register(selector, 0);
                return sk;
            } catch (IOException e) {
                throw new ChannelException("configure socket channel exception", e);
            }
        }

        boolean inputIsComplete() {
            try {
                int read = socketChannel.read(input);
                return read == 0;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        boolean outputIsComplete() {
            return !output.hasRemaining();
        }

        void process() throws UnsupportedEncodingException {
            System.out.println("msg received");
            input.flip();
            int len = input.remaining();
            byte[] bytes = new byte[len];
            input.get(bytes, 0, bytes.length);
            System.out.println(new String(bytes, "utf-8"));
            input.clear();
        }

        void read() {
            try {
                socketChannel.read(input);
                if (inputIsComplete()) {
                    process();
                    state = SENDING;
                    selectionKey.interestOps(SelectionKey.OP_WRITE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void send() {
            try {
                socketChannel.write(output);
                System.out.println("write msg finished");
                if (outputIsComplete()) {
                    selectionKey.cancel();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        }
    }
}
