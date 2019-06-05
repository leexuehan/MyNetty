package async;

import java.util.EventListener;

/**
 * @author leexuehan on 2019/6/5.
 */
public interface GenericFutureListener extends EventListener {
    void operationComplete();
}
