package inc.evil.serde.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ValueCastUtil {
    private static final Map<Class<?>, Function<Object, Object>> CASTING_FUNCTION = new HashMap<>();

    static {
        CASTING_FUNCTION.put(boolean.class, (value) -> (boolean) value);
        CASTING_FUNCTION.put(Boolean.class, (value) -> (boolean) value);
        CASTING_FUNCTION.put(byte.class, (value) -> ((Number) value).byteValue());
        CASTING_FUNCTION.put(Byte.class, (value) -> ((Number) value).byteValue());
        CASTING_FUNCTION.put(char.class, (value) -> (char) (((Number) value).intValue()));
        CASTING_FUNCTION.put(Character.class, (value) -> (char) (((Number) value).intValue()));
        CASTING_FUNCTION.put(short.class, (value) -> ((Number) value).shortValue());
        CASTING_FUNCTION.put(Short.class, (value) -> ((Number) value).shortValue());
        CASTING_FUNCTION.put(int.class, (value) -> ((Number) value).intValue());
        CASTING_FUNCTION.put(Integer.class, (value) -> ((Number) value).intValue());
        CASTING_FUNCTION.put(long.class, (value) -> ((Number) value).longValue());
        CASTING_FUNCTION.put(Long.class, (value) -> ((Number) value).longValue());
        CASTING_FUNCTION.put(float.class, (value) -> ((Number) value).floatValue());
        CASTING_FUNCTION.put(Float.class, (value) -> ((Number) value).floatValue());
        CASTING_FUNCTION.put(double.class, (value) -> ((Number) value).doubleValue());
        CASTING_FUNCTION.put(Double.class, (value) -> ((Number) value).doubleValue());
    }

    public Object castValueTo(Object instance, Class<?> targetType) {
        if (instance == null)
            return null;
        return CASTING_FUNCTION.getOrDefault(targetType, (value) -> value).apply(instance);
    }
}
