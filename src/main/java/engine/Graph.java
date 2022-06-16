package engine;

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

    Set<Node> deadNode = new HashSet<>();
    Set<Node> sourceNode = new HashSet<>();
    Set<Node> processNode = new HashSet<>();
    Set<Node> sinkNode = new HashSet<>();

    List<NodeConfig> nodeConfigs = Collections.emptyList();

    public Graph() {
    }

    public void addNode(Node node) {
        nodeMap.put(node.getName(), node);
    }

    void transform() {
        for (NodeConfig config : nodeConfigs) {
            String name = config.getName();
            try {
                Node node = new Node();
                Operator operator = classMap.get(name).newInstance();
                operator.setNode(node);

                node.setName(name);
                node.setOperator(operator);
                addNode(node);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    void perf() {
        Iterator<Map.Entry<String, Node>> iterator = nodeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Node> entry = iterator.next();
            Node Node = entry.getValue();
            List<Node> consumers = Node.getConsumers();

            if (Node.getDepends() == 0 && (consumers == null || consumers.isEmpty())) {
                iterator.remove();
                deadNode.add(Node);
                continue;
            }

            if (Node.getDepends() == 0) {
                sourceNode.add(Node);
                continue;
            }

            if (consumers == null || consumers.isEmpty()) {
                sinkNode.add(Node);
                continue;
            }

            processNode.add(Node);
        }
    }

    void generate() {
        nodeMap.values().forEach(node -> {
            node.setEngine(this);
            node.getOperator().register();
        });
    }

    public void build() {
        transform();
        generate();
        perf();
    }

    public void start() {
        sourceNode.forEach(Executor::execute);
    }

    public static void stop() {
        Executor.stop();
    }

    public Node getNode(String name) {
        return nodeMap.get(name);
    }
}