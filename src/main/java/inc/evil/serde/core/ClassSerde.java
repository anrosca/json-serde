package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.SerializerDeserializer;

public class ClassSerde implements SerializerDeserializer {
    @Override
    public JsonNode serialize(Object instance) {
        return new TextNode(((Class<?>) instance).getName());
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return clazz == Class.class;
    }

    @Override
    public Object deserialize(Class<?> resultingClass, JsonNode node) throws Exception {
        String className = node.asText();
        return Class.forName(className);
    }
}
