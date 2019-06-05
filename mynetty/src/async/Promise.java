package async;

/**
 * @author leexuehan on 2019/6/5.
 */

//可写的特殊的 Future
public interface Promise<V> extends Future {

    //设置成功，并通知所有的监听器
    Promise<V> setSuccess(V result);

    Promise<V> setFailure(Throwable cause);

    @Override
    Promise<V> addListener(GenericFutureListener listener);

    @Override
    Promise<V> removeListener(GenericFutureListener listener);

    @Override
    Promise<V> await() throws InterruptedException;
}
