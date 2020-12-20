package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerializerDeserializer;

import java.util.concurrent.atomic.AtomicBoolean;

public class BooleanSerde implements SerializerDeserializer {
    @Override
    public JsonNode serialize(Object instance, SerdeContext serdeContext) {
        if (instance instanceof Boolean) {
            return BooleanNode.valueOf((Boolean) instance);
        } else if (instance instanceof AtomicBoolean) {
            ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
            objectNode.set("type", new TextNode(instance.getClass().getName()));
            objectNode.set("value", BooleanNode.valueOf(((AtomicBoolean) instance).get()));
            return objectNode;
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
        return value.isBoolean() || (value.has("type") && value.get("type").asText().equals(AtomicBoolean.class.getName()));
    }

    @Override
    public Object deserialize(JsonNode node, SerdeContext serdeContext) throws Exception {
        if (node.has("type")) {
            return new AtomicBoolean(node.get("value").asBoolean());
        }
        return node.asBoolean();
    }

    @Override
    public Object deserialize(Class<?> resultingClass, JsonNode node, SerdeContext serdeContext) throws Exception {
        if (resultingClass == AtomicBoolean.class) {
            return new AtomicBoolean(node.asBoolean());
        }
        return node.asBoolean();
    }
}
