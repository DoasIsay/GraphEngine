package engine;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author xiewenwu
 */

public class Injector {
    public static void inject(Operator operator) {
        List<Reflect.AnnField> annFields = Reflect.getAnnField(operator, Depend.class);
        for (Reflect.AnnField annField: annFields) {
            Field field = annField.getField();
            Depend depend = annField.getAnn();
            String dependName = depend.name();

            try {
                Operator dependOperator = (Operator) field.get(operator);
                if (dependOperator != null) {
                    return;
                }
                dependOperator = operator.dependOn(dependName);
                field.set(operator, dependOperator);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
