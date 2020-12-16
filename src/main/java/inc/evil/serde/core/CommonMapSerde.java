package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.ObjectFactory;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerializerDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class CommonMapSerde implements SerializerDeserializer {
    private final ObjectFactory objectFactory = new ObjectFactory();
    private final SerdeContext serdeContext;

    public CommonMapSerde(SerdeContext serdeContext) {
        this.serdeContext = serdeContext;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JsonNode serialize(Object instance) {
        Map<Object, Object> mapToSerialize = (Map<Object, Object>) instance;
        ObjectNode mapNode = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode jsonNodes = new ArrayNode(JsonNodeFactory.instance);
        mapNode.set("type", new TextNode(instance.getClass().getName()));
        mapNode.set("value", jsonNodes);
        for (Map.Entry<Object, Object> entry : mapToSerialize.entrySet()) {
            ObjectNode mapEntryNode = new ObjectNode(JsonNodeFactory.instance);
            mapEntryNode.set("key", serdeContext.serializeValue(entry.getKey()));
            mapEntryNode.set("value", serdeContext.serializeValue(entry.getValue()));
            jsonNodes.add(mapEntryNode);
        }
        return mapNode;
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return clazz == HashMap.class || clazz == ConcurrentHashMap.class || clazz == TreeMap.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object deserialize(Class<?> resultingClass, JsonNode node) throws Exception {
        if (!node.isArray()) {
            return serdeContext.getNodeValue(node);
        }
        ArrayNode arrayNode = (ArrayNode) node;
        Map<Object, Object> map = (Map<Object, Object>) objectFactory.makeInstance(resultingClass);
        for (int i = 0; i < arrayNode.size(); ++i) {
            JsonNode currentNode = arrayNode.get(i);
            JsonNode keyNode = currentNode.get("key");
            JsonNode valueNode = currentNode.get("value");
            Object key = keyNode.isObject() ? serdeContext.deserialize(keyNode.toString(), resultingClass) : serdeContext.getNodeValue(keyNode);
            Object value = valueNode.isObject() ? serdeContext.deserialize(valueNode.toString(), resultingClass) : serdeContext.getNodeValue(valueNode);
            map.put(key, value);
        }
        return map;
    }
}
