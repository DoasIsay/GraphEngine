package engine;

import java.lang.reflect.Field;

/**
 * @author xiewenwu
 */

public class Injector {
    public static void inject(Operator operator) {
        Field[] fields = operator.getClass().getDeclaredFields();

        for (Field field: fields) {
            Depend[] depend = field.getDeclaredAnnotationsByType(Depend.class);
            if (depend == null || depend.length == 0) {
                continue;
            }

            field.setAccessible(true);
            try {
                Operator dependOperator = (Operator) field.get(operator);
                if (dependOperator != null) {
                    return;
                }

                String dependName = depend[0].name();
                dependOperator  = operator.dependOn(dependName);
                field.set(operator, dependOperator);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
