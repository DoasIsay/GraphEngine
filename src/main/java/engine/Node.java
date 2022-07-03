package engine;

import lombok.Data;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xiewenwu
 */
@Data
public class Node {
    boolean async;
    String name;

    AtomicInteger depends;
    AtomicInteger nodeDepends;
    AtomicInteger fieldDepends;

    List<Node> inNodes;
    Map<Node, List<String>> fieldInNodes;

    List<Node> outNodes;
    Map<String, List<Node>> fieldOutNodes;

    Operator operator;
    Graph graph;

    public Node() {
        depends = new AtomicInteger(0);
        nodeDepends = new AtomicInteger(0);
        fieldDepends = new AtomicInteger(0);

        inNodes = new ArrayList<>();
        outNodes = new ArrayList<>();

        fieldInNodes = new HashMap<>();
        fieldOutNodes = new HashMap<>();
    }

    public Node depend(String dependName) {
        String[] fields = dependName.split("\\.");

        Node node = null;
        if (fields.length == 1) {
            node = dependNode(fields[0]);
            this.incNodeDepends();
        } else if (fields.length == 2) {
            node = dependField(fields[0], fields[1]);
            this.incFieldDepends();
        }

        this.incDepends();
        return node;
    }

    public Node dependNode(String dependName) {
        Node dependNode = graph.getNode(dependName);
        if (dependNode == null) {
            throw new RuntimeException("not found node: " + dependName);
        }

        this.addInNode(dependNode);
        dependNode.addOutNode(this);
        return dependNode;
    }

    public Node dependField(String dependNodeName, String dependFieldName) {
        Node dependNode = graph.getNode(dependNodeName);
        if (dependNode == null) {
            throw new RuntimeException("not found node: " + dependNodeName);
        }

        if (!dependNode.getOperator().hasField(dependFieldName)) {
            throw new RuntimeException("not found node: " + dependNodeName + " field: " + dependFieldName);
        }

        this.addFieldInNode(dependFieldName, dependNode);
        dependNode.addFieldOutNode(dependFieldName, this);
        return dependNode;
    }

    void addInNode(Node node) {
        inNodes.add(node);
    }

    void addOutNode(Node node) {
        outNodes.add(node);
    }

    void addFieldInNode(String fieldName, Node node) {
        fieldInNodes.computeIfAbsent(node, k -> new ArrayList<>()).add(fieldName);
    }

    void addFieldOutNode(String fieldName, Node node) {
        fieldOutNodes.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(node);
    }

    void incDepends() {
        depends.incrementAndGet();
    }

    int decDepends() {
        if (inNodes.isEmpty() && fieldInNodes.isEmpty()) {
            return 0;
        }
        return depends.decrementAndGet();
    }

    void incNodeDepends() {
        nodeDepends.incrementAndGet();
    }

    void incFieldDepends() {
        fieldDepends.incrementAndGet();
    }

    public int decNodeDepends() {
        if (inNodes.isEmpty()) {
            return 0;
        }
        return nodeDepends.decrementAndGet();
    }

    public int decFieldDepends() {
        if (fieldInNodes.isEmpty()) {
            return 0;
        }

        return fieldDepends.decrementAndGet();
    }

    public int getDepends() {
        return depends.get();
    }

    public int getNodeDepends() {
        return nodeDepends.get();
    }

    public int getFieldDepends() {
        return fieldDepends.get();
    }

    public List<Node> getFieldOutNodes(String name) {
        return fieldOutNodes.getOrDefault(name, Collections.emptyList());
    }

    int calFieldDepends() {
        return fieldInNodes.entrySet().stream()
                .map(entry -> entry.getValue().size())
                .reduce((a, b) -> a + b)
                .orElse(0);
    }

    public void reset() {
        operator.clean();

        int fieldDependCount = calFieldDepends();
        depends.set(fieldDependCount + inNodes.size());
        nodeDepends.set(inNodes.size());
        fieldDepends.set(fieldDependCount);
    }

    public void check() {
        if (operator == null) {
            throw new RuntimeException(getName() + " has no operator");
        }
        if (graph == null) {
            throw new RuntimeException(getName() + " has no graph");
        }

        operator.check();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Operator) {
            return ((Node) obj).getName().equals(name);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return getName() + "(" + getInNodes().size() + ", " + getOutNodes().size() + ")";
    }
}
