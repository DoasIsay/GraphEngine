package engine;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xiewenwu
 */
public class Graph {
    @Setter
    List<NodeConfig> nodeConfigs = Collections.emptyList();

    Map<String, Node> nodeMap = new HashMap<>();
    @Getter
    List<Node> deadNodes = new ArrayList<>();
    @Getter
    List<Node> sourceNodes = new ArrayList<>();
    @Getter
    List<Node> processNodes = new ArrayList<>();
    @Getter
    List<Node> sinkNodes = new ArrayList<>();

    @Setter
    GraphPool graphPool = null;
    @Getter
    AtomicInteger running = new AtomicInteger(0);

    public Graph() {
    }

    public Node getNode(String name) {
        return nodeMap.get(name);
    }

    public Graph addNode(Node node) {
        node.check();
        nodeMap.put(node.getName(), node);
        return this;
    }

    Graph transform() {
        nodeConfigs.forEach(nodeConfig -> {
            String operatorName = nodeConfig.getOperator();
            Operator operator = OperatorFactory.get(operatorName);
            Node node = new Node();
            node.setName(nodeConfig.getName());
            node.setGraph(this);
            node.setOperator(operator);
            if (operator != null) {
                operator.setNode(node);
            }
            addNode(node);
        });

        return this;
    }

    Graph analysis() {
        Iterator<Map.Entry<String, Node>> iterator = nodeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Node> entry = iterator.next();
            Node node = entry.getValue();
            List<Node> consumers = node.getOutNodes();

            if (node.getNodeDepends() == 0 && (consumers == null || consumers.isEmpty())) {
                iterator.remove();
                deadNodes.add(node);
                continue;
            }
            if (node.getNodeDepends() == 0) {
                sourceNodes.add(node);
                continue;
            }
            if (consumers == null || consumers.isEmpty()) {
                sinkNodes.add(node);
                continue;
            }

            processNodes.add(node);
        }

        running.set(nodeMap.size());
        return this;
    }

    Graph generate() {
        nodeMap.values().forEach(node -> {
            Operator operator = node.getOperator();
            if (operator == null) {
                return;
            }
            operator.register();
            Injector.inject(operator);
        });
        return this;
    }

    public Graph build() {
        transform();
        generate();
        analysis();
        check();
        return this;
    }

    public Graph reset() {
        running.set(nodeMap.size());
        nodeMap.values().forEach(Node::reset);
        return this;
    }

    Graph check() {
        Set<Node> nodes = new HashSet<>(nodeMap.values());
        removeSourceNode(sourceNodes, nodes);
        if (!nodes.isEmpty()) {
            throw new RuntimeException("graph has cycle, caused by those nodes: " + nodes);
        }

        reset();
        return this;
    }

    public void clean() {
        nodeConfigs = null;
        nodeMap = null;
        deadNodes = null;
        sourceNodes = null;
        processNodes = null;
        sinkNodes = null;
        running = null;
    }

    public <T> void run(T value) {
        sourceNodes.forEach(node -> Executor.execute(node, value));
    }

    public void close() {
        if (decRunning() == 0 && graphPool != null) {
            System.out.println("graph run out of node, now close it");
            graphPool.returnResource(this);
        }
    }

    public int decRunning() {
        return running.decrementAndGet();
    }

    public boolean isRunning() {
        return running.get() != 0;
    }

    void removeSourceNode(List<Node> sourceNodes, Set<Node> nodes) {
        sourceNodes.forEach(sourceNode -> {
            if (sourceNode.getNodeDepends() != 0 || sourceNode.getFieldDepends() != 0) {
                return;
            }
            nodes.remove(sourceNode);
            sourceNode.getOutNodes().forEach(node -> {node.decNodeDepends();node.decFieldDepends();});
            removeSourceNode(sourceNode.getOutNodes(), nodes);
        });
    }

    StringBuilder padding(StringBuilder sb, int size) {
        for (int i = 0; i < size; ++i) {
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