package inc.evil.serde;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.cast.PrimitiveTypeCaster;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

class JsonSerde implements SerdeContext {
    private static final String REFERENCE_TO_OBJECT = "__ref";
    private static final String FIELD_ID = "__id";

    private final Map<Object, JsonNode> serializedInstances = new IdentityHashMap<>();
    private final Map<String, Object> deserializedInstances = new HashMap<>();
    private final AtomicLong fieldIdGenerator = new AtomicLong();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<SerializerDeserializer> serializerDeserializers;

    public JsonSerde(List<SerializerDeserializer> serializerDeserializers) {
        this.serializerDeserializers = serializerDeserializers;
    }

    public String serialize(Object instance) {
        return toJson(instance).toPrettyString();
    }

    private JsonNode toJson(Object instance) {
        try {
            if (wasSerialized(instance)) {
                return getPreviouslySerializedInstance(instance);
            }
            return trySerializeToJson(instance);
        } catch (Exception e) {
            throw new JsonSerializationException(e);
        }
    }

    public JsonNode getPreviouslySerializedInstance(Object instance) {
        JsonNode visitedInstance = serializedInstances.get(instance);
        ObjectNode referenceNode = new ObjectNode(JsonNodeFactory.instance);
        referenceNode.set("type", new TextNode(REFERENCE_TO_OBJECT));
        referenceNode.set("value", new TextNode(visitedInstance.get(FIELD_ID).asText()));
        return referenceNode;
    }

    private JsonNode trySerializeToJson(Object instance) {
        for (SerializerDeserializer serde : serializerDeserializers) {
            if (serde.canConsume(instance.getClass())) {
                return serde.serialize(instance, this);
            }
        }
        throw new IllegalStateException("No serializer found for class: " + instance.getClass());
    }

    @Override
    public Object getPreviouslyDeserializedInstance(String value) {
        return deserializedInstances.get(value);
    }

    public boolean wasSerialized(Object instance) {
        return serializedInstances.containsKey(instance);
    }

    @Override
    public void addSerializedInstance(Object instance, ObjectNode rootNode) {
        serializedInstances.put(instance, rootNode);
    }

    @Override
    public JsonNode serializeValue(Object instance) {
        for (SerializerDeserializer serde : serializerDeserializers) {
            if (serde.canConsume(instance != null ? instance.getClass() : null)) {
                return serde.serialize(instance, this);
            }
        }
        return toJson(instance);
    }

    public <T> T deserialize(String json, Class<T> clazz) {
        try {
            if (isNull(json)) {
                return null;
            }
            return tryDeserialize(json, clazz);
        } catch (Exception e) {
            throw new JsonDeserializationException(e);
        }
    }

    @Override
    public void addDeserializedInstance(String objectId, Object instance) {
        deserializedInstances.put(objectId, instance);
    }

    @Override
    public long generateObjectId() {
        return fieldIdGenerator.incrementAndGet();
    }

    @SuppressWarnings("unchecked")
    private <T> T tryDeserialize(String json, Class<T> resultingType) throws Exception {
        JsonNode rootNode = objectMapper.readTree(json);
        if (rootNode.has("type")) {
            return (T) getValueAs(rootNode.get("value"), rootNode.get("type").asText());
        }
        for (SerializerDeserializer serde : serializerDeserializers) {
            if (serde.canConsume(rootNode)) {
                return castValueTo(serde.deserialize(rootNode, this), resultingType);
            }
        }
        throw new IllegalStateException("No deserializer found for class: " + resultingType.getName());
    }

    private boolean isNull(String json) {
        return json == null || json.equals("null");
    }

    private <T> T castValueTo(Object instance, Class<T> targetType) {
        PrimitiveTypeCaster castUtil = new PrimitiveTypeCaster();
        return castUtil.castValueTo(instance, targetType);
    }

    public Object deserializeValue(JsonNode fieldNode) throws Exception {
        for (SerializerDeserializer serde : serializerDeserializers) {
            if (serde.canConsume(fieldNode)) {
                return serde.deserialize(fieldNode, this);
            }
        }
        JsonNode value = fieldNode.get("value");
        JsonNode type = fieldNode.get("type");
        return getValueAs(value, type.asText());
    }

    private Object getValueAs(JsonNode value, String type) throws Exception {
        if (type != null) {
            if (type.equals(REFERENCE_TO_OBJECT)) {
                return deserializedInstances.get(value.asText());
            }
            Class<?> resultingClass = Class.forName(type);
            for (SerializerDeserializer serde : serializerDeserializers) {
                if (serde.canConsume(resultingClass)) {
                    return serde.deserialize(resultingClass, value, this);
                }
                if (serde.canConsume(value)) {
                    return serde.deserialize(resultingClass, value, this);
                }
            }
            return deserialize(value.toString(), resultingClass);
        }
        throw new IllegalStateException("Cannot deserialize json node because of missing type information. Json: " + value);
    }
}
