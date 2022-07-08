package engine;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xiewenwu
 */
@Setter
@Getter
public class Node {
    boolean async;
    String name;
    int timeout = 10;
    NodeConfig config;

    AtomicInteger depends;
    Map<String, Node> inNodes;
    Map<String, Node> outNodes;

    Operator operator;

    Graph graph;

    public Node() {
        depends = new AtomicInteger(0);
        inNodes = new HashMap<>();
        outNodes = new HashMap<>();
    }

    public Node depend(Node dependNode) {
        this.addInNode(dependNode);
        dependNode.addOutNode(this);
        return dependNode;
    }

    void addInNode(Node node) {
        if (!inNodes.containsKey(node.getName())) {
            this.incDepends();
            inNodes.put(node.getName(), node);
        }
    }

    public Collection<Node> getInNodes() {
        return inNodes.values();
    }

    void addOutNode(Node node) {
        if (!outNodes.containsKey(node.getName())) {
            outNodes.put(node.getName(), node);
        }
    }

    public Collection<Node> getOutNodes() {
        return outNodes.values();
    }

    void incDepends() {
        depends.incrementAndGet();
    }

    public int decDepends() {
        if (inNodes.isEmpty()) {
            return 0;
        }

        return depends.decrementAndGet();
    }

    public int getDepends() {
        return depends.get();
    }

    public void reset() {
        operator.clean();
        if (inNodes == null) {
            depends.set(0);
            return;
        }
        depends.set(inNodes.size());
    }

    public void check() {
        if (operator == null) {
            throw new RuntimeException(getName() + " has no operator");
        }

        operator.check();
    }

    public <T> T getOperator(String name) {
        Node node = inNodes.get(name);
        if (node != null) {
            return (T) node.getOperator();
        }

        throw new RuntimeException("not found node: " + name);
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
