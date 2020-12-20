package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerializerDeserializer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicNumbersSerde implements SerializerDeserializer {
    @Override
    public JsonNode serialize(Object instance, SerdeContext serdeContext) {
        Number number = (Number) instance;
        ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
        objectNode.set("type", new TextNode(instance.getClass().getName()));
        objectNode.set("value", new LongNode(number.longValue()));
        return objectNode;
    }

    @Override
    public boolean canConsume(JsonNode node) {
        if (node.has("type")) {
            String typeName = node.get("type").asText();
            return typeName.equals(AtomicLong.class.getName()) || typeName.equals(AtomicInteger.class.getName());
        }
        return false;
    }

    @Override
    public Object deserialize(JsonNode node, SerdeContext serdeContext) throws Exception {
        String typeName = node.get("type").asText();
        return deserialize(Class.forName(typeName), node.get("value"), serdeContext);
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return clazz == AtomicInteger.class || clazz == AtomicLong.class;
    }

    @Override
    public Object deserialize(Class<?> resultingClass, JsonNode node, SerdeContext serdeContext) throws Exception {
        Number number = node.asLong();
        if (resultingClass == AtomicInteger.class) {
            return new AtomicInteger(number.intValue());
        } else if (resultingClass == AtomicLong.class) {
            return new AtomicLong(number.longValue());
        }
        throw new IllegalStateException(resultingClass.getName() + " can't be deserialized by " + getClass().getCanonicalName());
    }
}
