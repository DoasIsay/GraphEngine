package engine;

import lombok.Data;

import java.util.List;
import java.util.Optional;

/**
 * @author xiewenwu
 */
@Data
public abstract class Operator<T> {
    Node node;

    public abstract void invoke(T value);

    public void register() {}

    public <O extends Operator> O depend(String dependName) {
        return (O) Optional.ofNullable(node.depend(dependName))
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

    List<Reflect.AnnotationField> fields;
    public void clean() {
        if (fields == null) {
            fields = Reflect.getAnnotationField(this, Output.class);
        }

        Reflect.clean(this, fields);
    }
}
