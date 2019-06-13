package tech.netty.async;


import tech.netty.NettyChannel;

/**
 * @author leexuehan on 2019/6/5.
 */
public interface ChannelFuture extends Future {
    NettyChannel channel();

    @Override
    ChannelFuture addListener(GenericFutureListener listener);

    @Override
    ChannelFuture sync() throws InterruptedException;

    @Override
    ChannelFuture await() throws InterruptedException;
}
