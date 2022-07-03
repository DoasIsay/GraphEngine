package engine;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    Map<String, Reflect.AnnotationField> name2FieldMap;

    public boolean hasField(String name) {
        if (fields == null) {
            fields = Reflect.getAnnotationField(this, Output.class);
            name2FieldMap = fields.stream().collect(Collectors.toMap(field -> field.getField().getName(), v -> v));
        }

        return name2FieldMap.containsKey(name);
    }

    public <T> void emit(String fieldName, Object fieldValue, T value) {
        if (fields == null) {
            fields = Reflect.getAnnotationField(this, Output.class);
            name2FieldMap = fields.stream().collect(Collectors.toMap(field -> field.getField().getName(), v -> v));
        }

        try {
            Reflect.setFieldValue(this, name2FieldMap.get(fieldName).getField(), fieldValue);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (value != null) {
                Executor.notify(this.getNode(),fieldName, value);
            }
        }
    }

    public void clean() {
        if (fields == null) {
            fields = Reflect.getAnnotationField(this, Output.class);
        }

        Reflect.clean(this, fields);
    }
}
