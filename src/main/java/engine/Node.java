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
    Graph engine;
    AtomicInteger depends;
    List<Node> inNodes;
    List<Node> outNodes;
    Operator operator;

    public Node() {
        depends = new AtomicInteger(0);
        inNodes = new ArrayList<>();
        outNodes = new ArrayList<>();
    }

    public Node dependOn(String dependName) {
        this.incDepends();
        Node dependNode = engine.getNode(dependName);
        inNodes.add(dependNode);
        dependNode.addOutNode(this);
        return dependNode;
    }

    void addOutNode(Node consumer) {
        outNodes.add(consumer);
    }

    void incDepends() {
        depends.incrementAndGet();
    }

    public int decDepends() {
        return depends.decrementAndGet();
    }

    public int getDepends() {
        return depends.get();
    }

    public void resetDepends() {
        if (inNodes == null) {
            depends.set(0);
            return;
        }
        depends.set(inNodes.size());
    }

    public void register() {
        operator.register();
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
}
