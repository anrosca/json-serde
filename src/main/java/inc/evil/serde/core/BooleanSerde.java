package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import inc.evil.serde.SerializerDeserializer;

import java.util.concurrent.atomic.AtomicBoolean;

public class BooleanSerde implements SerializerDeserializer {
    @Override
    public JsonNode serialize(Object instance) {
        if (instance instanceof Boolean) {
            return BooleanNode.valueOf((Boolean) instance);
        }
        return BooleanNode.valueOf(((AtomicBoolean) instance).get());
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return clazz == AtomicBoolean.class || clazz == Boolean.class;
    }

    @Override
    public boolean canConsume(JsonNode value) {
        return value.isBoolean();
    }

    @Override
    public Object deserialize(JsonNode node) throws Exception {
        return node.asBoolean();
    }

    @Override
    public Object deserialize(Class<?> resultingClass, JsonNode node) throws Exception {
        return node.asBoolean();
    }
}
