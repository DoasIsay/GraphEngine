package engine;

import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xiewenwu
 */
public class Executor {
    static public void stop() {
        threadPoolExecutor.shutdown();
    }

    static ThreadPoolExecutor threadPoolExecutor= new ThreadPoolExecutor(
            1,
            64,
            10,
            TimeUnit.MINUTES,
            new SynchronousQueue<>());

    static void notify(List<Operator> operators) {
        operators.forEach(operator -> {
            if (operator.decDepend() == 0) {
                System.out.println("notify " + operator.getName());
                Executor.execute(operator);
                notify(operator.getConsumers());
            }
        });
    }

    public static void execute(Operator operator) {
        if (operator.isAsync()) {
            threadPoolExecutor.submit(() -> {
                operator.invoke();
                notify(operator.getConsumers());
            });
        } else {
            operator.invoke();
            notify(operator.getConsumers());
        }
    }
}
