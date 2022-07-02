package engine;

import java.util.Collections;
import java.util.Map;

/**
 * @author xiewenwu
 */
public class OperatorFactory {
    static Map<String, Class<?>> classMap = Collections.emptyMap();

    static {
        classMap = Reflect.getAnnotationClass("", Load.class).get("operator");
    }

    public static <T extends Operator> T get(String name) {
        Class c = classMap.get(name);

        if (c == null) {
            return null;
        }

        try {
            return (T) c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
