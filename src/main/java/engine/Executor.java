package engine;

import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xiewenwu
 */

public class Executor {
    static ThreadPoolExecutor threadPoolExecutor= new ThreadPoolExecutor(
            1,
            64,
            10,
            TimeUnit.MINUTES,
            new SynchronousQueue<>());

    static void notify(List<Node> nodes) {
        nodes.forEach(node -> {
            if (node.decDepend() == 0) {
                System.out.println("notify " + node.getName());
                Executor.execute(node);
                notify(node.getConsumers());
            }
        });
    }

    public static void execute(Node node) {
        if (node.isAsync()) {
            threadPoolExecutor.submit(() -> {
                node.getOperator().invoke();
                notify(node.getConsumers());
            });
        } else {
            node.getOperator().invoke();
            notify(node.getConsumers());
        }
    }

    public static void stop() {
        threadPoolExecutor.shutdown();
    }
}
