package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import inc.evil.serde.ObjectFactory;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerializerDeserializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

public class CommonCollectionSerde implements SerializerDeserializer {
    private final ObjectFactory objectFactory = new ObjectFactory();
    private final SerdeContext serdeContext;

    public CommonCollectionSerde(SerdeContext serdeContext) {
        this.serdeContext = serdeContext;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JsonNode serialize(Object instance) {
        Collection<Object> collection = (Collection<Object>) instance;
        ArrayNode jsonNodes = new ArrayNode(JsonNodeFactory.instance);
        for (Object item : collection) {
            jsonNodes.add(serdeContext.serializeValue(item));
        }
        return jsonNodes;
    }

    @Override
    public boolean canConsume(Class<?> type) {
        return type == ArrayList.class || type == HashSet.class || type == LinkedList.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object deserialize(Class<?> resultingClass, JsonNode node) throws Exception {
        ArrayNode arrayNode = (ArrayNode) node;
        Collection<Object> collection = (Collection<Object>) objectFactory.makeInstance(resultingClass);
        for (int i = 0; i < arrayNode.size(); ++i) {
            JsonNode currentNode = arrayNode.get(i);
            Object value = currentNode.isObject() ? serdeContext.deserialize(currentNode.toString(), resultingClass) : serdeContext.getNodeValue(currentNode);
            collection.add(value);
        }
        return collection;
    }
}
