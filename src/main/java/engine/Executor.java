package engine;

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

    static <T> void invoke(Node node, T value) {
        Operator operator = node.getOperator();
        try {
            operator.invoke(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            notify(node, value);
            node.getGraph().close();
        }
    }

    public static void stop() {
        threadPoolExecutor.shutdown();
    }
}
