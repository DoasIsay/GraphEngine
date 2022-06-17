package engine;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @author xiewenwu
 */

@Getter
@Setter
public class Graph {
    Map<String, Class<? extends Operator>> classMap = new HashMap<>();
    Map<String, Node> nodeMap = new HashMap<>();

    List<Node> deadNodes = new ArrayList<>();
    List<Node> sourceNodes = new ArrayList<>();
    List<Node> processNodes = new ArrayList<>();
    List<Node> sinkNodes = new ArrayList<>();

    List<NodeConfig> nodeConfigs = Collections.emptyList();

    public Graph() {
    }

    public void addNode(Node node) {
        nodeMap.put(node.getName(), node);
    }

    void transform() {
        nodeConfigs.forEach(nodeConfig -> {
            String name = nodeConfig.getName();
            try {
                Node node = new Node();
                Operator operator = classMap.get(name).newInstance();
                operator.setNode(node);
                node.setName(name);
                node.setEngine(this);
                node.setOperator(operator);
                addNode(node);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    void perf() {
        Iterator<Map.Entry<String, Node>> iterator = nodeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Node> entry = iterator.next();
            Node node = entry.getValue();
            List<Node> consumers = node.getOutNodes();

            if (node.getDepends() == 0 && (consumers == null || consumers.isEmpty())) {
                iterator.remove();
                deadNodes.add(node);
                continue;
            }

            if (node.getDepends() == 0) {
                sourceNodes.add(node);
                continue;
            }

            if (consumers == null || consumers.isEmpty()) {
                sinkNodes.add(node);
                continue;
            }

            processNodes.add(node);
        }
    }

    void generate() {
        nodeMap.values().forEach(Node::register);
    }

    public void build() {
        transform();
        generate();
        perf();
        check();
    }

    public void start() {
        sourceNodes.forEach(Executor::execute);
    }

    public static void stop() {
        Executor.stop();
    }

    public Node getNode(String name) {
        return nodeMap.get(name);
    }

    void check() {
        Map<String, Node> tmp = new HashMap<>(nodeMap);
        removeSourceNode(sourceNodes, tmp);
        if (!tmp.isEmpty()) {
            throw new RuntimeException("graph has cycle: " + JSON.toJSONString(tmp));
        }

        nodeMap.values().forEach(Node::resetDepends);
    }

    void removeSourceNode(List<Node> sourceNodes, Map<String, Node> nodeMap) {
        sourceNodes.forEach(sourceNode -> {
            if (sourceNode.getDepends() == 0) {
                nodeMap.remove(sourceNode.getName());
                sourceNode.getOutNodes().forEach(Node::decDepends);
                removeSourceNode(sourceNode.getOutNodes(), nodeMap);
            }
        });
    }
}