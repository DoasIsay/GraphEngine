package engine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author xiewenwu
 */
public class Reflect {
    public static void setDefFieldValue(Object obj, Field field) {
        field.setAccessible(true);
        Class type = field.getType();

        try {
            if (type.isAssignableFrom(char.class) || type.isAssignableFrom(Character.class)) {
                field.set(obj, '0');
            } else if (type.isAssignableFrom(short.class) || type.isAssignableFrom(Short.class)) {
                field.set(obj, (short) 0);
            } else if (type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class)) {
                field.set(obj, 0);
            } else if (type.isAssignableFrom(long.class) || type.isAssignableFrom(Long.class)) {
                field.set(obj, (long) 0);
            } else if (type.isAssignableFrom(byte.class) || type.isAssignableFrom(Byte.class)) {
                field.set(obj, (byte) 0);
            } else if (type.isAssignableFrom(boolean.class) || type.isAssignableFrom(Boolean.class)) {
                field.set(obj, false);
            } else if (type.isAssignableFrom(float.class) || type.isAssignableFrom(Float.class)) {
                field.set(obj, (float) 0);
            } else if (type.isAssignableFrom(double.class) || type.isAssignableFrom(Double.class)) {
                field.set(obj, (double) 0);
            } else if (type.isAssignableFrom(String.class)) {
                field.set(obj, "");
            } else if (type.isAssignableFrom(Map.class)) {
                field.set(obj, Collections.emptyMap());
            } else if (type.isAssignableFrom(Set.class)) {
                field.set(obj, Collections.emptySet());
            } else if (type.isAssignableFrom(List.class)) {
                field.set(obj, Collections.emptyList());
            } else {
                field.set(obj, type.newInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clean(Object obj, List<Field> fields) {
        if (fields == null || fields.isEmpty()) {
            return;
        }

        fields.forEach(field -> setDefFieldValue(obj, field));
    }

    public static List<Field> getAnnotationField(Object obj, Class annClass) {
        Field[] fields = Optional.ofNullable(obj)
                .map(tmp -> tmp.getClass().getDeclaredFields())
                .get();

        List<Field> annFields = new ArrayList<>();
        for (Field field: fields) {
            Annotation[] annotation = field.getDeclaredAnnotationsByType(annClass);
            if (annotation == null || annotation.length == 0) {
                continue;
            }

            annFields.add(field);
        }

        return annFields;
    }
}
