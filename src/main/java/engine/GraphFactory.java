package engine;

import lombok.Setter;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.List;
import java.util.Map;

/**
 * @author xiewenwu
 */
public class GraphFactory implements PooledObjectFactory<Graph> {
    @Setter
    Map<String, Class<? extends Operator>> classMap;
    @Setter
    List<NodeConfig> nodeConfigs;

    public GraphFactory(Map<String, Class<? extends Operator>> classMap, List<NodeConfig> nodeConfigs) {
        this.classMap = classMap;
        this.nodeConfigs = nodeConfigs;
    }

    @Override
    public PooledObject<Graph> makeObject() throws Exception {
        Graph graph = new Graph();
        graph.setClassMap(classMap);
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
