package engine;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiewenwu
 */
public class Reflect {
    public static void setDefFieldValue(Object obj, AnnField annField) {
        Field field = annField.getField();
        OutPut outPut = annField.getAnn();
        String value = outPut.value();
        Class type = field.getType();

        boolean hasValue = value != null && !value.isEmpty();
        try {
            if (type.isAssignableFrom(char.class) || type.isAssignableFrom(Character.class)) {
                if (hasValue) {
                    field.set(obj, value.charAt(0));
                } else {
                    field.set(obj, '0');
                }
            } else if (type.isAssignableFrom(short.class) || type.isAssignableFrom(Short.class)) {
                if (hasValue) {
                    field.set(obj, Short.valueOf(value).shortValue());
                } else {
                    field.set(obj, (short) 0);
                }
            } else if (type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class)) {
                if (hasValue) {
                    field.set(obj, Integer.valueOf(value).intValue());
                } else {
                    field.set(obj, (int) 0);
                }
            } else if (type.isAssignableFrom(long.class) || type.isAssignableFrom(Long.class)) {
                if (hasValue) {
                    field.set(obj, Long.valueOf(value).longValue());
                } else {
                    field.set(obj, (long) 0);
                }
            } else if (type.isAssignableFrom(byte.class) || type.isAssignableFrom(Byte.class)) {
                if (hasValue) {
                    field.set(obj, Byte.valueOf(value).byteValue());
                } else {
                    field.set(obj, (byte) 0);
                }

            } else if (type.isAssignableFrom(boolean.class) || type.isAssignableFrom(Boolean.class)) {
                if (hasValue) {
                    field.set(obj, Boolean.valueOf(value).booleanValue());
                } else {
                    field.set(obj, false);
                }
            } else if (type.isAssignableFrom(float.class) || type.isAssignableFrom(Float.class)) {
                if (hasValue) {
                    field.set(obj, Float.valueOf(value));
                } else {
                    field.set(obj, (float) 0);
                }
            } else if (type.isAssignableFrom(double.class) || type.isAssignableFrom(Double.class)) {
                if (hasValue) {
                    field.set(obj, Double.valueOf(value).doubleValue());
                } else {
                    field.set(obj, (double) 0);
                }
            } else if (type.isAssignableFrom(String.class)) {
                field.set(obj, value);
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

    public static void clean(Object obj, List<AnnField> annFields) {
        if (annFields == null || annFields.isEmpty()) {
            return;
        }

        annFields.forEach(field -> setDefFieldValue(obj, field));
    }

    @Data
    @AllArgsConstructor
    public static class AnnField {
        Object ann;
        Field field;
        public <T extends Annotation> T getAnn() {
            return (T) ann;
        }
    }

    public static List<AnnField> getAnnField(Object obj, Class annClass) {
        Field[] fields = Optional.ofNullable(obj)
                .map(tmp -> tmp.getClass().getDeclaredFields())
                .get();

        List<AnnField> annFields = new ArrayList<>();
        for (Field field : fields) {
            Annotation[] annotation = field.getDeclaredAnnotationsByType(annClass);
            if (annotation == null || annotation.length == 0) {
                continue;
            }

            field.setAccessible(true);
            annFields.add(new AnnField(annotation[0], field));
        }

        return annFields;
    }

    public static List<Field> getField(Object obj, Class annClass) {
        return getAnnField(obj, annClass)
                .stream()
                .map(AnnField::getField)
                .collect(Collectors.toList());
    }
}

