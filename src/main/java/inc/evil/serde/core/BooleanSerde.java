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
        } else if (instance instanceof AtomicBoolean) {
            return BooleanNode.valueOf(((AtomicBoolean) instance).get());
        }
        throw new IllegalArgumentException(instance.getClass().getName() +
                                           " can't be serialized by " + getClass().getCanonicalName());
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
        if (resultingClass == AtomicBoolean.class) {
            return new AtomicBoolean(node.asBoolean());
        }
        return node.asBoolean();
    }
}
