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
    Map<String, Class<? extends Operator>> classMap = Collections.emptyMap();
    Map<String, Node> nodeMap = new HashMap<>();

    List<Node> deadNodes = new ArrayList<>();
    List<Node> sourceNodes = new ArrayList<>();
    List<Node> processNodes = new ArrayList<>();
    List<Node> sinkNodes = new ArrayList<>();

    List<NodeConfig> nodeConfigs = Collections.emptyList();

    public Graph() {
    }

    public void addNode(Node node) {
        if (nodeMap == null) {
            nodeMap = new HashMap<>();
        }
        nodeMap.put(node.getName(), node);
    }

    void transform() {
        nodeConfigs.forEach(nodeConfig -> {
            String name = nodeConfig.getName();
            try {
                Node node = new Node();
                Operator operator = classMap.get(name).newInstance();

                node.setName(name);
                node.setGraph(this);
                node.setOperator(operator);

                operator.setNode(node);
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
        nodeMap.values().forEach(node -> node.getOperator().register());
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
        Set<Node> nodes = new HashSet<>(nodeMap.values());
        removeSourceNode(sourceNodes, nodes);
        if (!nodes.isEmpty()) {
            throw new RuntimeException("graph has cycle: " + nodes);
        }

        nodeMap.values().forEach(Node::resetDepends);
    }

    void removeSourceNode(List<Node> sourceNodes, Set<Node> nodes) {
        sourceNodes.forEach(sourceNode -> {
            if (sourceNode.getDepends() == 0) {
                nodes.remove(sourceNode);
                sourceNode.getOutNodes().forEach(Node::decDepends);
                removeSourceNode(sourceNode.getOutNodes(), nodes);
            }
        });
    }

    StringBuilder padding(StringBuilder sb, int size) {
        for (int i=0; i < size; ++i) {
            sb.append(" ");
        }
        return sb;
    }

    void toString(StringBuilder father, List<Node> nodes) {
        Iterator<Node> iterable = nodes.iterator();
        while (iterable.hasNext()) {
            Node node = iterable.next();
            StringBuilder child = new StringBuilder(father);
            father.append(" -> ").append(node);
            toString(father, node.getOutNodes());

            int start = child.lastIndexOf("\n");
            int end = child.length();
            if (iterable.hasNext()) {
                father.append(" -> end").append("\n");
                padding(father, end - start - 1);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("graph:\n");

        sourceNodes.forEach(node -> {
            StringBuilder path = new StringBuilder();
            path.append("    start -> ").append(node);
            toString(path, node.getOutNodes());
            path.append(" -> end").append("\n\n");
            sb.append(path);
        });

        return sb.toString();
    }
}