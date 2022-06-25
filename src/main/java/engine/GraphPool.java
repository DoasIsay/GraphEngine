package engine;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xiewenwu
 */
public class GraphPool {
    GenericObjectPool pool;
    HashedWheelTimer timer;

    public GraphPool(Map<String, Class<? extends Operator>> classMap, List<NodeConfig> nodeConfigs) {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMinIdle(1);
        genericObjectPoolConfig.setMaxIdle(6);
        genericObjectPoolConfig.setMaxTotal(100);//取值：qps+1000ms/最大异步算子耗时(ms)
        genericObjectPoolConfig.setLifo(true);
        genericObjectPoolConfig.setBlockWhenExhausted(true);
        genericObjectPoolConfig.setTestOnBorrow(false);
        genericObjectPoolConfig.setTestOnCreate(false);
        pool = new GenericObjectPool(new GraphFactory(classMap, nodeConfigs), genericObjectPoolConfig);
        timer = new HashedWheelTimer();
    }

    public Graph getResource() throws Exception {
        return (Graph) pool.borrowObject();
    }

    public void returnResource(Graph graph) {
        if (graph == null) {
            return;
        }

        if (!graph.isRunning()) {
            pool.returnObject(graph);
            return;
        }

        //graph中存在异步算子还没跑完延迟归还
        delayReturn(graph);
    }

    void delayReturn(Graph graph) {
        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                //还没跑完直接丢掉
                if (graph.isRunning()) {
                    return;
                }
                returnResource(graph);
            }
        }, 10, TimeUnit.MILLISECONDS);
    }

    public String toString() {
        return pool.toString();
    }
}
