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
    private final SerdeContext serdeContext;
    private final List<SerializerDeserializer> delegates;
    private final LambdaSerde lambdaSerde;

    public ObjectSerde(SerdeContext serdeContext, List<SerializerDeserializer> delegates) {
        this.serdeContext = serdeContext;
        this.delegates = delegates;
        this.lambdaSerde = new LambdaSerde(this);
    }

    public ObjectSerde(SerdeContext serdeContext) {
        this.serdeContext = serdeContext;
        this.delegates = Collections.emptyList();
        this.lambdaSerde = new LambdaSerde(this);
    }

    @Override
    public JsonNode serialize(Object instance) {
        return null;
    }

    @Override
    public JsonNode serialize(Object instance, Class<?> type) {
        for (SerializerDeserializer serde : delegates) {
            if (serde.canConsume(type)) {
                return serde.serialize(instance);
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
    public Object deserialize(Class<?> resultingClass, JsonNode node) throws Exception {
        return serdeContext.deserialize(node.toString(), resultingClass);
    }

    @Override
    public Object deserialize(JsonNode node) throws Exception {
        JsonNode type = node.get("type");
        String className = type.asText();
        if (className != null) {
            if (className.contains("/")) {
                return lambdaSerde.deserialize(node.get("value"));
            }
            Class<?> resultingClass = Class.forName(className);
            return serdeContext.deserialize(node.toString(), resultingClass);
        }
        throw new RuntimeException("ALARM");
    }
}
