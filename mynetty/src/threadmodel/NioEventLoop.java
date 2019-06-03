package threadmodel;

import common.ChannelException;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

//事件循环执行器
public class NioEventLoop {
    private Thread thread;
    private Queue<Runnable> queue;
    private Selector selector;

    public NioEventLoop(int maxPendingTasks) {
        this.queue = newTaskQueue(maxPendingTasks);
        this.selector = openSelector();
    }

    protected LinkedBlockingQueue<Runnable> newTaskQueue(int maxPendingTasks) {
        return new LinkedBlockingQueue<>(maxPendingTasks);
    }

    private Selector openSelector() {
        try {
            return Selector.open();
        } catch (IOException e) {
            throw new ChannelException("server socket open selector exception", e);
        }
    }

    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        addTask(task);
        if (inEventLoop()) {
            startThread();
        }
    }

    private void startThread() {
        this.run();
    }

    private void run() {
        thread = Thread.currentThread();
        for (; ; ) {
            try {
                if (hasTasks()) {
                    this.selector.selectNow();
                } else {
                    processSelectedKeys();
                    runAllTasks();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //从任务队列中取出消息执行
    private void runAllTasks() {

    }

    private void processSelectedKeys() {

    }

    private boolean hasTasks() {
        return !this.queue.isEmpty();
    }

    private boolean inEventLoop() {
        return thread == Thread.currentThread();
    }

    private void addTask(Runnable task) {
        if (!queue.offer(task)) {
            throw new RejectedExecutionException("task rejected");
        }
    }
}
