package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.SerializerDeserializer;

public class StringSerde implements SerializerDeserializer {
    @Override
    public JsonNode serialize(Object instance) {
        return new TextNode((String) instance);
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return clazz == String.class;
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
        return value.asText().equals("null") ? null : value.asText();
    }
}
