package engine;

import lombok.Setter;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.List;

/**
 * @author xiewenwu
 */
public class GraphFactory implements PooledObjectFactory<Graph> {
    @Setter
    List<NodeConfig> nodeConfigs;

    public GraphFactory(List<NodeConfig> nodeConfigs) {
        this.nodeConfigs = nodeConfigs;
    }

    @Override
    public PooledObject<Graph> makeObject() throws Exception {
        Graph graph = new Graph();
        graph.setNodeConfigs(nodeConfigs);
        graph.build();

        return new DefaultPooledObject<>(graph);
    }

    @Override
    public void destroyObject(PooledObject<Graph> pooledObject) throws Exception {
        if (pooledObject == null) {
            return;
        }

        Graph graph = pooledObject.getObject();
        if (graph != null) {
            graph.clean();
        }
    }

    @Override
    public boolean validateObject(PooledObject<Graph> pooledObject) {
        if (pooledObject == null) {
            return false;
        }

        Graph graph = pooledObject.getObject();
        if (graph != null) {
            try {
                graph.check();
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void activateObject(PooledObject<Graph> pooledObject) throws Exception {
        if (pooledObject == null) {
            return;
        }

        Graph graph = pooledObject.getObject();
        if (graph != null) {
            graph.reset();
        }
    }

    @Override
    public void passivateObject(PooledObject<Graph> pooledObject) throws Exception {
    }
}
