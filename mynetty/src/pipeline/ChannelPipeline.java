package pipeline;

/**
 * @author leexuehan on 2019/6/5.
 */
public interface ChannelPipeline {
    ChannelPipeline addLast(String name, ChannelHandler handler);
}
