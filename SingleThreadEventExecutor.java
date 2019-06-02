package netty;

import java.util.Queue;
import java.util.concurrent.*;

/**
 * Developed by Lee Happily.
 */
public abstract class SingleThreadEventExecutor {
    private Thread thread;
    private Queue<Runnable> taskQueue;
    private Executor executor;

    private final RejectedExecutionHandler rejectedExecutionHandler;

    public SingleThreadEventExecutor(int maxPendingTasks, Executor executor,
                                     RejectedExecutionHandler rejectedExecutionHandler) {
        this.taskQueue = newTaskQueue(maxPendingTasks);
        this.executor = executor;
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }

    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return new LinkedBlockingQueue<>(maxPendingTasks);
    }

    /**
     * 程序执行入口
     */
    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }

        addTask(task);

        if (threadNotStart()) {
            startThread();
        }

    }

    private void startThread() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                thread = Thread.currentThread();
                SingleThreadEventExecutor.this.run();
            }
        });
    }


    protected abstract void run();

    private boolean threadNotStart() {
        return thread != Thread.currentThread();
    }

    private void addTask(Runnable task) {
        if (!offerTask(task)) {
            reject(task);
        }
    }

    private boolean offerTask(Runnable task) {
        return taskQueue.offer(task);
    }


    protected static void reject() {
        throw new RejectedExecutionException("event executor terminated");
    }

    protected final void reject(Runnable task) {
//        rejectedExecutionHandler.rejectedExecution(task, this);
        throw new RejectedExecutionException("task rejected");
    }
}
