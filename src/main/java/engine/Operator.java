package engine;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xiewenwu
 */

@Data
public abstract class Operator {
    boolean async = true;
    String name;
    Graph engine;
    AtomicInteger depends;
    List<Operator> consumers;

    public Operator() {
        depends = new AtomicInteger(0);
        consumers = new ArrayList<>();
    }

    public abstract void invoke();

    public abstract void register();

    public <T extends Operator> T dependOn(String dependName) {
        this.incDepend();
        Operator dependOperator = engine.getOperator(dependName);
        dependOperator.addConsumer(this);

        return (T) dependOperator;
    }

    void addConsumer(Operator consumer) {
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
            return ((Operator) obj).getName().equals(name);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}