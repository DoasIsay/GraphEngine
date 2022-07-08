package engine;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author xiewenwu
 */
public class Injector {
    public static void inject(Graph graph, Operator operator) {
        List<Reflect.AnnotationField> annotationFields = operator.getDependAnnotationField();
        for (Reflect.AnnotationField annotationField : annotationFields) {
            Field field = annotationField.getField();
            Depend depend = annotationField.getAnnotation();
            String dependName = depend.name();

            try {
                Operator dependOperator = (Operator) field.get(operator);
                if (dependOperator != null) {
                    return;
                }

                Node dependNode = graph.getNode(dependName);
                if (dependNode == null) {
                    return;
                }

                dependOperator = dependNode.getOperator();
                if (dependOperator != null) {
                    field.set(operator, dependOperator);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
