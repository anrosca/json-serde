package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import inc.evil.serde.SerializerDeserializer;

public class NullSerde implements SerializerDeserializer {
    @Override
    public JsonNode serialize(Object instance) {
        return NullNode.getInstance();
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return clazz == null;
    }

    @Override
    public boolean canConsume(JsonNode node) {
        return node.isNull();
    }

    @Override
    public Object deserialize(JsonNode node) throws Exception {
        return null;
    }

    @Override
    public Object deserialize(Class<?> resultingClass, JsonNode node) throws Exception {
        return null;
    }
}
