package engine;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xiewenwu
 */

public class Cleaner {
    static void setFieldValue(Object obj, Field field) {
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
            }  else if (type.isAssignableFrom(List.class)) {
                field.set(obj, Collections.emptyList());
            } else {
                field.set(obj,type.newInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clean(Object obj, Field[] fields) {
        if (fields == null) {
            fields = obj.getClass().getDeclaredFields();
        }

        for (Field field: fields) {
            OutPut[] outPut = field.getDeclaredAnnotationsByType(OutPut.class);
            if (outPut == null || outPut.length == 0) {
                continue;
            }

            System.out.println(field.getName());
            setFieldValue(obj, field);
        }
    }

}
