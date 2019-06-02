package netty;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;

/**
 * Developed by Lee Happily.
 */
public class NioEventLoop extends SingleThreadEventExecutor {

    public NioEventLoop(int maxPendingTasks, Executor executor, RejectedExecutionHandler rejectedExecutionHandler) {
        super(maxPendingTasks, executor, rejectedExecutionHandler);
    }

    @Override
    protected void run() {

    }
}
