package engine;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xiewenwu
 */
@Data
public class Node {
    boolean async;
    String name;
    int timeout = 10;
    AtomicInteger depends;
    List<Node> inNodes;
    List<Node> outNodes;
    Operator operator;
    Graph graph;

    public Node() {
        depends = new AtomicInteger(0);
        inNodes = new ArrayList<>();
        outNodes = new ArrayList<>();
    }

    public Node depend(String dependName) {
        this.incDepends();
        Node dependNode = graph.getNode(dependName);
        if (dependNode == null) {
            throw new RuntimeException("not found node: " + dependName);
        }

        this.addInNode(dependNode);
        dependNode.addOutNode(this);
        return dependNode;
    }

    void addInNode(Node node) {
        inNodes.add(node);
    }

    void addOutNode(Node node) {
        outNodes.add(node);
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
