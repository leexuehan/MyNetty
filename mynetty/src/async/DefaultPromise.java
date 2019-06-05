package async;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @author leexuehan on 2019/6/5.
 */

//Promise 的默认实现
public class DefaultPromise<V> implements Promise<V> {
    private static final AtomicReferenceFieldUpdater<DefaultPromise, Object> RESULT_UPDATER =
            AtomicReferenceFieldUpdater.newUpdater(DefaultPromise.class, Object.class, "result");

    private volatile Object result;
    private static final Object SUCCESS = new Object();
    private static final Object UNCANCELLABLE = new Object();

    private short waiters;

    @Override
    public Promise<V> setSuccess(V result) {
        Object value = result == null ? SUCCESS : result;
        if (RESULT_UPDATER.compareAndSet(this, null, value) ||
                RESULT_UPDATER.compareAndSet(this, UNCANCELLABLE, value)) {

        }

        return null;
    }

    @Override
    public Promise<V> setFailure(Throwable cause) {
        return null;
    }

    @Override
    public Promise<V> addListener(GenericFutureListener listener) {
        return null;
    }

    @Override
    public Promise<V> removeListener(GenericFutureListener listener) {
        return null;
    }

    @Override
    public Promise<V> await() throws InterruptedException {
        if (isDone()) {
            return this;
        }
        if (Thread.interrupted()) {
            throw new InterruptedException(toString());
        }

        synchronized (this) {
            while (!isDone()) {
                incWaiters();
                try {
                    wait();
                } finally {
                    decWaiters();
                }
            }
        }
        return this;
    }

    private void decWaiters() {
        waiters--;
    }

    private void incWaiters() {
        waiters++;
    }

    @Override
    public Future sync() throws InterruptedException {
        return null;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
