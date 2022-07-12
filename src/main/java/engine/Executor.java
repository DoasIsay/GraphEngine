package engine;

import io.netty.util.Timeout;

import java.util.concurrent.*;

/**
 * @author xiewenwu
 */
public class Executor {
    static ThreadPoolExecutor threadPoolExecutor= new ThreadPoolExecutor(
            1,
            Runtime.getRuntime().availableProcessors(),
            1,
            TimeUnit.MINUTES,
            new SynchronousQueue<>(),
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
                    runnable.run();
                }
            });

    static <T> void notify(Node father, T value) {
        if (!father.setState(1)) {
            return;
        }

        father.getOutNodes().forEach(node -> {
            System.out.println(father.getName() + " notify: " + node.getName() + ", depend: " + (node.getDepends() - 1));
            if (node.decDepends() != 0) {
                return;
            }

            Executor.execute(node, value);
        });
    }

    public static <T> void execute(Node node, T value) {
        if (node.isSync() && node.getOutNodes().size() == 1) {
            invoke(node, value);
        } else {
            node.getGraph().addFuture(threadPoolExecutor.submit(() -> invoke(node, value)));
        }
    }

    public static Future submit(Runnable runnable) {
        return threadPoolExecutor.submit(runnable);
    }

    /*
     * 为什么不使用threadPoolExecutor.submit(() -> invoke(node, value))返回的Future来控制超时？
     * 不符合依赖数据驱动调度的思想，强行使用会导致代码及调度复杂化，不利于代码的可读性，可维护性
     *
     * 采用netty高性能的HashedWheelTimer定时器来控制算子的超时
     * 会存在定时器过多影响性能？
     * Tcp发包每发一个包都会设置一个超时重传定时器，定时器应该不是问题，仅对有网络访问的算子设定超时控制
     *
     */

    static <T> void invoke(Node node, T value) {
        Timeout timeout = null;
        if (0 != node.getTimeout()) {
            Thread thread = Thread.currentThread();
            timeout = Timer.timeout(node.getName(), ()->{ thread.interrupt(); notify(node, value);}, node.getTimeout());
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
