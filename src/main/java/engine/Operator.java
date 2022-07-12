package engine;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author xiewenwu
 */
@Getter
@Setter
public abstract class Operator<T> {
    public abstract void invoke(T value);

    List<Reflect.AnnotationField> outputFields;

    public List<Reflect.AnnotationField> getOutputAnnotationField() {
        if (outputFields == null) {
            outputFields = Reflect.getAnnotationField(this, Output.class);
        }

        return outputFields;
    }

    List<Reflect.AnnotationField> dependFields;

    public List<Reflect.AnnotationField> getDependAnnotationField() {
        if (dependFields == null) {
            dependFields = Reflect.getAnnotationField(this, Depend.class);
        }

        return dependFields;
    }

    public void clean() {
        Reflect.clean(this, getOutputAnnotationField());
    }

    public void check() {

    }
}
