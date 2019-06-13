package tech.netty.async;

/**
 * @author leexuehan on 2019/6/5.
 */

//netty 中的 Future 重写了 JDK 中的 Future，使得任务返回结果的判定粒度更细
public interface Future extends java.util.concurrent.Future {
    boolean isSuccess();

    Throwable cause();

    Future addListener(GenericFutureListener listener);

    Future removeListener(GenericFutureListener listener);

    /**
     * 等待直到操作完成，如果操作失败，则此方法会将失败原因再次抛出
     */
    Future sync() throws InterruptedException;

    /**
     * 等待操作完成
     */
    Future await() throws InterruptedException;
}
