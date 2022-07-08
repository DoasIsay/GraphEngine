package engine;

import io.netty.util.Timeout;

import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xiewenwu
 */
public class Executor {
    static ThreadPoolExecutor threadPoolExecutor= new ThreadPoolExecutor(
            1,
            Runtime.getRuntime().availableProcessors(),
            1,
            TimeUnit.MINUTES,
            new SynchronousQueue<>());

    static <T> void notify(Node father, T value) {
        father.getOutNodes().forEach(node -> {
            System.out.println(father.getName() + " notify: " + node.getName() + ", depend: " + (node.getDepends() - 1));
            if (node.decDepends() != 0) {
                return;
            }

            Executor.execute(node, value);
        });
    }

    public static <T> void execute(Node node, T value) {
        if (node.isAsync() && node.getOutNodes().size() > 1) {
            threadPoolExecutor.submit(() -> invoke(node, value));
        } else {
            invoke(node, value);
        }
    }

    /*
     * 仅对有网络访问的算子设定超时控制
     * 会存在定时器过多影响性能？
     * 定时器采用netty高性能的HashedWheelTimer
     * Tcp发包每发一个包都会设置一个超时重传定时器，Tcp的性能因此而不行了？
     * 实际性况待压测
     */

    static <T> void invoke(Node node, T value) {
        Timeout timeout = null;
        if (0 != node.getTimeout()) {
            timeout = Timer.timeout(node.getName(), node.getTimeout());
        }

        try {
            node.getOperator().invoke(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (timeout != null) {
                timeout.cancel();
            }
            notify(node, value);
            node.getGraph().close();
        }
    }

    public static void stop() {
        threadPoolExecutor.shutdown();
    }
}
