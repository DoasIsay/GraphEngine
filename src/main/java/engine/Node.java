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
    List<Node> consumers;
    Operator operator;

    public Node() {
        depends = new AtomicInteger(0);
        consumers = new ArrayList<>();
    }

    public Node dependOn(String dependName) {
        this.incDepend();
        Node dependNode = engine.getNode(dependName);
        dependNode.addConsumer(this);

        return dependNode;
    }

    void addConsumer(Node consumer) {
        consumers.add(consumer);
    }

    void incDepend() {
        depends.incrementAndGet();
    }

    public int decDepend() {
        return depends.decrementAndGet();
    }

    public int getDepends() {
        return depends.get();
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
