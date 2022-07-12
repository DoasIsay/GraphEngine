package engine;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @author xiewenwu
 */
public class GraphPool {
    GenericObjectPool pool;

    public GraphPool(GraphConfig config) {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMinIdle(1);
        genericObjectPoolConfig.setMaxIdle(6);
        genericObjectPoolConfig.setMaxTotal(100);//取值：qps+1000ms/最大异步算子耗时(ms)
        genericObjectPoolConfig.setLifo(true);
        genericObjectPoolConfig.setBlockWhenExhausted(true);
        genericObjectPoolConfig.setTestOnBorrow(false);
        genericObjectPoolConfig.setTestOnCreate(false);
        pool = new GenericObjectPool(new GraphFactory(config), genericObjectPoolConfig);
    }

    public Graph getResource() throws Exception {
        Graph graph = (Graph) pool.borrowObject();
        graph.setGraphPool(this);

        return graph;
    }

    public void returnResource(Graph graph) {
        if (graph == null) {
            return;
        }

        if (graph.isRunning()) {
            throw new RuntimeException("invalid state graph is still running " + graph.getRunning());
        }

        graph.setGraphPool(null);
        pool.returnObject(graph);
    }

    @Override
    public String toString() {
        return pool.toString();
    }
}
