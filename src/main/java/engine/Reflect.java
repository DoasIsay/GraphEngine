package engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiewenwu
 */
public class Reflect {
    public static void clean(Object obj, List<AnnotationField> annotationFields) {
        if (annotationFields == null || annotationFields.isEmpty()) {
            return;
        }

        annotationFields.forEach(field -> setDefFieldValue(obj, field));
    }

    @Data
    @AllArgsConstructor
    public static class AnnotationField {
        Annotation annotation;
        Field field;
        public <T extends Annotation> T getAnnotation() {
            return (T) annotation;
        }
    }

    public static List<AnnotationField> getAnnotationField(Object obj, Class annClass) {
        Field[] fields = Optional.ofNullable(obj)
                .map(tmp -> tmp.getClass().getDeclaredFields())
                .get();

        List<AnnotationField> annotationFields = new ArrayList<>();
        for (Field field : fields) {
            Annotation[] annotation = field.getDeclaredAnnotationsByType(annClass);
            if (annotation == null || annotation.length == 0) {
                continue;
            }

            field.setAccessible(true);
            annotationFields.add(new AnnotationField(annotation[0], field));
        }

        return annotationFields;
    }

    public static List<Field> getField(Object obj, Class annClass) {
        return getAnnotationField(obj, annClass)
                .stream()
                .map(AnnotationField::getField)
                .collect(Collectors.toList());
    }

    public static <T> T getAnnotationValue(Class target, Class ann, String field) {
        try {
            Annotation annotation = target.getDeclaredAnnotation(ann);
            if (annotation == null) {
                return null;
            }
            Method method = ann.getMethod(field);
            return (T) method.invoke(annotation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getAnnotationName(Class target, Class ann) {
        String name = getAnnotationValue(target, ann, "name");
        if (name != null && name.isEmpty()) {
            return target.getSimpleName();
        }

        return name;
    }

    public static String getAnnotationType(Class target, Class ann) {
        return getAnnotationValue(target, ann, "type");
    }

    static Map<Class, Map<String, Map<String, Class<?>>>> annTypeName2ClassMap = new HashMap<>();

    public static Map<String, Map<String, Class<?>>> getAnnotationClass(String path, Class ann) {
        Map<String, Map<String, Class<?>>> typeName2ClassMap = annTypeName2ClassMap.get(ann);
        if (typeName2ClassMap != null) {
            return typeName2ClassMap;
        }

        Reflections reflections = new Reflections(path);
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(ann);

        typeName2ClassMap = new HashMap<>();
        for (Class classType: classSet) {
            String name = getAnnotationName(classType, ann);
            if (name == null) {
                continue;
            }

            String type = getAnnotationType(classType, ann);
            System.out.println(path + " get annotation: " + name + "\tclassType: " + classType.getName());

            Map<String, Class<?>> name2ClassMap = typeName2ClassMap.computeIfAbsent(type, k -> new HashMap<>());
            Class tmp = name2ClassMap.get(name);
            if (tmp != null) {
                System.out.println("class name: " + name + " has tow classType old class: " + classType.getName() + " new class: "+ tmp.getName() + " please check");
            }
            name2ClassMap.put(name, classType);
        }

        return typeName2ClassMap;
    }

    public static void setDefFieldValue(Object obj, AnnotationField annotationField) {
        Field field = annotationField.getField();
        Output outPut = annotationField.getAnnotation();
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
                    field.set(obj, Short.valueOf(value));
                } else {
                    field.set(obj, (short) 0);
                }
            } else if (type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class)) {
                if (hasValue) {
                    field.set(obj, Integer.valueOf(value));
                } else {
                    field.set(obj, (int) 0);
                }
            } else if (type.isAssignableFrom(long.class) || type.isAssignableFrom(Long.class)) {
                if (hasValue) {
                    field.set(obj, Long.valueOf(value));
                } else {
                    field.set(obj, (long) 0);
                }
            } else if (type.isAssignableFrom(byte.class) || type.isAssignableFrom(Byte.class)) {
                if (hasValue) {
                    field.set(obj, Byte.valueOf(value));
                } else {
                    field.set(obj, (byte) 0);
                }
            } else if (type.isAssignableFrom(boolean.class) || type.isAssignableFrom(Boolean.class)) {
                if (hasValue) {
                    field.set(obj, Boolean.valueOf(value));
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
                    field.set(obj, Double.valueOf(value));
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
}