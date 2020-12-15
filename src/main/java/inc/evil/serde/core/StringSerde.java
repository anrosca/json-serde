package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.SerializerDeserializer;

public class StringSerde implements SerializerDeserializer {

    @Override
    public JsonNode serialize(Object instance) {
        if (instance instanceof CharSequence) {
            return new TextNode(instance.toString());
        }
        throw new IllegalArgumentException(instance.getClass().getName() +
                                           " can't be serialized by " + getClass().getName());
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return clazz == String.class || clazz == StringBuilder.class || clazz == StringBuffer.class;
    }

    @Override
    public boolean canConsume(JsonNode node) {
        return node.isTextual();
    }

    @Override
    public Object deserialize(JsonNode node) throws Exception {
        return node.asText();
    }

    @Override
    public Object deserialize(Class<?> resultingClass, JsonNode value) throws Exception {
        String resultingString = value.asText().equals("null") ? null : value.asText();
        return castTo(resultingClass, resultingString);
    }

    private Object castTo(Class<?> resultingClass, String string) {
        if (resultingClass == StringBuilder.class) {
            return new StringBuilder(string);
        } else if (resultingClass == StringBuffer.class) {
            return new StringBuffer(string);
        }
        return string;
    }
}
