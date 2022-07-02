package engine;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author xiewenwu
 */
public class Injector {
    public static void inject(Operator operator) {
        List<Reflect.AnnotationField> annotationFields = Reflect.getAnnotationField(operator, Depend.class);
        for (Reflect.AnnotationField annotationField : annotationFields) {
            Field field = annotationField.getField();
            Depend depend = annotationField.getAnnotation();
            String dependName = depend.name();

            try {
                Operator dependOperator = (Operator) field.get(operator);
                if (dependOperator != null) {
                    return;
                }
                dependOperator = operator.depend(dependName);
                field.set(operator, dependOperator);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
