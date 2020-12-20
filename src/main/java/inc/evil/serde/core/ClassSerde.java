package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerializerDeserializer;

public class ClassSerde implements SerializerDeserializer {
    @Override
    public JsonNode serialize(Object instance, SerdeContext serdeContext) {
        if (instance instanceof Class) {
            ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
            objectNode.set("type", new TextNode(Class.class.getName()));
            objectNode.set("value", new TextNode(((Class<?>) instance).getName()));
            return objectNode;
        }
        throw new IllegalArgumentException(instance.getClass().getName() + " can't be serialized by " + getClass().getName());
    }

    @Override
    public boolean canConsume(JsonNode node) {
        return node.has("type") && node.get("type").asText().equals(Class.class.getName());
    }

    @Override
    public Object deserialize(JsonNode node, SerdeContext serdeContext) throws Exception {
        if (node.get("value").isNull()) {
            return null;
        }
        return Class.forName(node.get("value").asText());
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return clazz == Class.class;
    }

    @Override
    public Object deserialize(Class<?> resultingClass, JsonNode node, SerdeContext serdeContext) throws Exception {
        if (node.isObject()) {
            return deserialize(node, serdeContext);
        }
        String className = node.asText();
        return Class.forName(className);
    }
}
