package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.SerializerDeserializer;

import java.lang.reflect.Method;

public class EnumSerde implements SerializerDeserializer {

    @Override
    public JsonNode serialize(Object instance) {
        return new TextNode(instance.toString());
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
        Method valueOfMethod = enumClass.getDeclaredMethod("valueOf", String.class);
        valueOfMethod.setAccessible(true);
        return (Enum<?>) valueOfMethod.invoke(null, enumValue);
    }
}
