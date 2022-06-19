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
            Runtime.getRuntime().availableProcessors(),
            1,
            TimeUnit.MINUTES,
            new SynchronousQueue<>());

    static void notify(List<Node> nodes) {
        nodes.forEach(node -> {
            if (node.decDepends() == 0) {
                System.out.println("notify " + node.getName());
                Executor.execute(node);
                notify(node.getOutNodes());
            }
        });
    }

    public static void execute(Node node) {
        if (node.isAsync()) {
            threadPoolExecutor.submit(() -> {
                node.getOperator().invoke();
                System.out.println(node.getName());
                notify(node.getOutNodes());
            });
        } else {
            node.getOperator().invoke();
            System.out.println(node.getName());
            notify(node.getOutNodes());
        }
    }

    public static void stop() {
        threadPoolExecutor.shutdown();
    }
}
