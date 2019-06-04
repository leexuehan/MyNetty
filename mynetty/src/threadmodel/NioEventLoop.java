package threadmodel;

import common.ChannelException;
import common.NioServerSocketChannel;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

//事件循环执行器
public class NioEventLoop {
    private Thread thread;
    private Queue<Runnable> queue;
    private Executor executor;
    private Selector selector;

    public NioEventLoop(int maxPendingTasks, Executor executor) {
        this.queue = newTaskQueue(maxPendingTasks);
        this.selector = openSelector();
        this.executor = executor;
    }

    public Selector getSelector() {
        return selector;
    }

    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return new LinkedBlockingQueue<>(maxPendingTasks);
    }

    private Selector openSelector() {
        try {
            return Selector.open();
        } catch (IOException e) {
            throw new ChannelException("server socket open selector exception", e);
        }
    }

    public void register(NioServerSocketChannel channel) {
        this.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    channel.doRegister(NioEventLoop.this);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        addTask(task);
        System.out.println("add task finished!");
        if (!inEventLoop()) {
            startThread();
        }
    }

    private void startThread() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                NioEventLoop.this.run();
            }
        });
    }

    private void run() {
        thread = Thread.currentThread();
        System.out.println("start looping");
        for (; ; ) {
            try {
                if (hasTasks()) {
                    this.selector.selectNow();
                } else {
                    // if there is no task then select directly
                    doSelectTask();
                }
                try {
                    processSelectedKeys();
                } finally {
                    runAllTasks();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void doSelectTask() {
        Selector selector = this.selector;
        try {
            long currentTimeNanos = System.nanoTime();
            long selectDeadlineNanos = currentTimeNanos + TimeUnit.SECONDS.toNanos(1);
            for (; ; ) {
                long timeoutMillis = (selectDeadlineNanos - currentTimeNanos + 500_000L) / 1_000_000L;
                //超时则执行 select 方法返回
                if (timeoutMillis < 0) {
                    selector.selectNow();
                    break;
                }
                //如果有任务，为了防止任务一直处于等待状态，需要及时返回
                if (hasTasks()) {
                    selector.selectNow();
                    break;
                }

                //阻塞执行 select 方法直到超时
                int selectKeys = selector.select(timeoutMillis);

                //有新事件发生
                if (selectKeys != 0) {
                    break;
                }

                //线程被打断，返回
                if (Thread.interrupted()) {
                    break;
                }

                currentTimeNanos = System.nanoTime();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //从任务队列中取出消息执行
    private void runAllTasks() {
        while (!queue.isEmpty()) {
            System.out.println("run tasks from queue");
            Runnable task = queue.poll();
            try {
                if (task == null) {
                    break;
                }
                task.run();
            } catch (Throwable e) {
                System.err.println("task execute error!");
            }
        }
    }

    private void processSelectedKeys() {
        Set<SelectionKey> selectionKeySet = this.selector.selectedKeys();
        if (selectionKeySet.isEmpty()) {
            return;
        }
        System.out.println("keyset not empty");
        Iterator<SelectionKey> iterator = selectionKeySet.iterator();
        for (; ; ) {
            SelectionKey key = iterator.next();
            iterator.remove();
            NioServerSocketChannel channel = (NioServerSocketChannel) key.attachment();
            processSelectedKey(key, channel);
            if (!iterator.hasNext()) {
                break;
            }
        }
    }

    private void processSelectedKey(SelectionKey key, NioServerSocketChannel channel) {
        int readyOps = key.readyOps();
        if ((readyOps & SelectionKey.OP_CONNECT) != 0) {
            System.out.println("connect event!!!!!");
            int ops = key.interestOps();
            ops &= ~SelectionKey.OP_CONNECT;
            key.interestOps(ops);
        }

        if ((readyOps & SelectionKey.OP_WRITE) != 0) {
            System.out.println("write event!!!");
        }

        if ((readyOps & (SelectionKey.OP_ACCEPT | SelectionKey.OP_READ)) != 0 || readyOps == 0) {
            System.out.println("Accept new connection");
            try {
                SocketChannel socketChannel = channel.accept();
            } catch (IOException e) {
                throw new ChannelException("accept new channel exception", e);
            }
        }
    }

    private boolean hasTasks() {
        return !this.queue.isEmpty();
    }

    private boolean inEventLoop() {
        return thread == Thread.currentThread();
    }

    private void addTask(Runnable task) {
        System.out.println("add new task to queue");
        if (!queue.offer(task)) {
            throw new RejectedExecutionException("task rejected");
        }
    }
}
