package pipeline;

/**
 * @author leexuehan on 2019/6/5.
 */
public class DefaultChannelHandlerContext extends AbstractChannelHandlerContext {
    private final ChannelHandler handler;

    public DefaultChannelHandlerContext(DefaultChannelPipeline pipeline, ChannelHandler handler, String name) {
        super(pipeline, handler.getClass(), name);
        this.handler = handler;
    }

    @Override
    public ChannelHandler handler() {
        return handler;
    }
}
