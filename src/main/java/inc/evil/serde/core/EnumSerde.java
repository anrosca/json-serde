package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.SerializerDeserializer;

import java.lang.reflect.Method;

public class EnumSerde implements SerializerDeserializer {

    @Override
    public JsonNode serialize(Object instance) {
        if (instance.getClass().isEnum()) {
            return new TextNode(instance.toString());
        }
        throw new IllegalArgumentException(instance.getClass().getName() + " can't be serialized by " + getClass().getName());
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return clazz.isEnum();
    }

    @Override
    public Object deserialize(Class<?> enumClass, JsonNode node) throws Exception {
        String enumValue = node.asText();
        if (enumValue == null || enumValue.equals("null")) {
            return null;
        }
        return tryGetEnumValue(enumClass, enumValue);
    }

    private Enum<?> tryGetEnumValue(Class<?> enumClass, String enumValue) {
        try {
            Method valueOfMethod = enumClass.getDeclaredMethod("valueOf", String.class);
            valueOfMethod.setAccessible(true);
            return (Enum<?>) valueOfMethod.invoke(null, enumValue);
        } catch (Exception e) {
            throw new EnumDeserializationException("No such enum constant " + enumClass.getName() + "." + enumValue);
        }
    }

    public static class EnumDeserializationException extends RuntimeException {
        public EnumDeserializationException(String message) {
            super(message);
        }
    }
}
