package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerializerDeserializer;

import java.lang.reflect.Method;

public class EnumSerde implements SerializerDeserializer {

    @Override
    public JsonNode serialize(Object instance, SerdeContext serdeContext) {
        if (instance.getClass().isEnum()) {
            ObjectNode enumNode = new ObjectNode(JsonNodeFactory.instance);
            enumNode.set("type", new TextNode(instance.getClass().getName()));
            enumNode.set("value", new TextNode(instance.toString()));
            return enumNode;
        }
        throw new IllegalArgumentException(instance.getClass().getName() + " can't be serialized by " + getClass().getName());
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return clazz.isEnum();
    }

    @Override
    public Object deserialize(Class<?> enumClass, JsonNode node, SerdeContext serdeContext) throws Exception {
        if (node.isObject()) {
            String className = node.get("type").asText();
            JsonNode enumValue = node.get("value");
            return deserialize(Class.forName(className), enumValue, serdeContext);
        }
        String enumValue = node.asText();
        if (enumValue == null || enumValue.equals("null")) {
            return null;
        }
        return tryGetEnumValue(enumClass, enumValue, serdeContext);
    }

    private Enum<?> tryGetEnumValue(Class<?> enumClass, String enumValue, SerdeContext serdeContext) {
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
