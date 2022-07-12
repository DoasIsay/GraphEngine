package engine;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author xiewenwu
 */
public class Timer implements TimerTask {
    long start;
    String name;
    Thread thread;
    Future future;
    Runnable runnable;

    public Timer(String name, Thread thread) {
        this.name = name;
        this.thread = thread;
        start = System.currentTimeMillis();
    }

    public Timer(String name, Future future) {
        this.name = name;
        this.future = future;
        start = System.currentTimeMillis();
    }

    public Timer(String name, Runnable runnable) {
        this.name = name;
        this.runnable = runnable;
        start = System.currentTimeMillis();
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        System.out.println(name + " operator run timeout cost: " + (System.currentTimeMillis() - start));

        if (runnable != null) {
            runnable.run();
            return;
        }

        if (thread != null) {
            thread.interrupt();
            return;
        }

        if (future != null) {
            future.cancel(true);
            return;
        }
    }

    public static HashedWheelTimer innerTimer = new HashedWheelTimer(5, TimeUnit.MILLISECONDS);

    public static Timeout timeout(String name, long delay) {
        return innerTimer.newTimeout(new Timer(name, Thread.currentThread()), delay, TimeUnit.MILLISECONDS);
    }

    public static Timeout timeout(String name, Future future, long delay) {
        return innerTimer.newTimeout(new Timer(name, future), delay, TimeUnit.MILLISECONDS);
    }

    public static Timeout timeout(String name, Runnable runnable, long delay) {
        return innerTimer.newTimeout(new Timer(name, runnable), delay, TimeUnit.MILLISECONDS);
    }

    public static void stop() {
        innerTimer.stop();
    }
}
