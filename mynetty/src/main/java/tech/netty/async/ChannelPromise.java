package tech.netty.async;


import tech.netty.NettyChannel;

/**
 * @author leexuehan on 2019/6/5.
 */
public interface ChannelPromise extends ChannelFuture, Promise<Void> {
    @Override
    NettyChannel channel();

    @Override
    ChannelPromise addListener(GenericFutureListener listener);

    @Override
    ChannelPromise removeListener(GenericFutureListener listener);

    @Override
    ChannelPromise sync() throws InterruptedException;

    @Override
    ChannelPromise await() throws InterruptedException;

    @Override
    ChannelPromise setSuccess(Void result);

    @Override
    ChannelPromise setFailure(Throwable cause);
}
