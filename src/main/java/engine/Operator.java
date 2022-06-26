package engine;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * @author xiewenwu
 */

@Data
public abstract class Operator<T> {
    Node node;

    public abstract void invoke(T value);

    public abstract void register();

    public <O extends Operator> O dependOn(String dependName) {
        return (O) Optional.ofNullable(node.dependOn(dependName))
                .map(node -> node.getOperator())
                .orElse(null);
    }

    public String getName() {
        return node.getName();
    }

    public void check() {
        if (node == null) {
            throw new RuntimeException(this.getClass().getSimpleName() + " has no node");
        }
    }

    Field[] fields;
    public void clean() {
        if (fields == null) {
            fields = this.getClass().getDeclaredFields();
        }

        Cleaner.clean(this, fields);
    }
}
