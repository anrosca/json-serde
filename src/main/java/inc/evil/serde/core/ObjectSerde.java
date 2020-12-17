package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerializerDeserializer;

import java.util.Collections;
import java.util.List;

public class ObjectSerde implements SerializerDeserializer {
    private final List<SerializerDeserializer> delegates;
    private final LambdaSerde lambdaSerde;

    public ObjectSerde(List<SerializerDeserializer> delegates) {
        this.delegates = delegates;
        this.lambdaSerde = new LambdaSerde(this);
    }

    public ObjectSerde() {
        this.delegates = Collections.emptyList();
        this.lambdaSerde = new LambdaSerde(this);
    }

    @Override
    public JsonNode serialize(Object instance, SerdeContext serdeContext) {
        return null;
    }

    @Override
    public JsonNode serialize(Object instance, Class<?> type, SerdeContext serdeContext) {
        for (SerializerDeserializer serde : delegates) {
            if (serde.canConsume(type)) {
                return serde.serialize(instance, serdeContext);
            }
        }
        ObjectNode fieldNode = new ObjectNode(JsonNodeFactory.instance);
        fieldNode.set("type", new TextNode(instance != null ? instance.getClass().getName() : type.getName()));
        fieldNode.set("value", serdeContext.serializeValue(instance));
        return fieldNode;
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean canConsume(JsonNode node) {
        return node.isObject();
    }

    @Override
    public Object deserialize(Class<?> resultingClass, JsonNode node, SerdeContext serdeContext) throws Exception {
        return serdeContext.deserialize(node.toString(), resultingClass);
    }

    @Override
    public Object deserialize(JsonNode node, SerdeContext serdeContext) throws Exception {
        JsonNode type = node.get("type");
        String className = type.asText();
        if (className != null) {
            if (className.contains("/")) {
                return lambdaSerde.deserialize(node.get("value"), serdeContext);
            }
            Class<?> resultingClass = Class.forName(className);
            return serdeContext.deserialize(node.toString(), resultingClass);
        }
        throw new RuntimeException("ALARM");
    }
}
