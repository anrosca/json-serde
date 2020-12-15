package inc.evil.serde;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.core.*;
import inc.evil.serde.util.ValueCastUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

class JsonSerde implements SerdeContext {
    private static final String REFERENCE_TO_OBJECT = "__ref";
    private static final String FIELD_ID = "__id";

    private final Map<Object, JsonNode> serializedInstances = new IdentityHashMap<>();
    private final Map<String, Object> deserializedInstances = new HashMap<>();
    private final AtomicLong fieldIdGenerator = new AtomicLong();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectFactory objectFactory = new ObjectFactory();
    private final List<SerializerDeserializer> serializerDeserializers = List.of(
            new NullSerde(),
            new PrimitiveTypeSerde(this),
            new ArraySerde(this, List.of(new CommonMapSerde(this), new CommonCollectionSerde(this))),
            new CommonMapSerde(this),
            new CommonCollectionSerde(this),
            new CommonDateSerde(),
            new ClassSerde(),
            new AtomicNumbersSerde(),
            new BigNumbersSerde(),
            new EnumSerde(),
            new StringSerde(),
            new NumericSerde(this),
            new BooleanSerde(),
            new LambdaSerde(new ObjectSerde(this)),
            new ObjectSerde(this)
    );
    private final ObjectSerializer objectSerializer = new ObjectSerializer(this);

    public String serialize(Object instance) {
        return toJson(instance).toPrettyString();
    }

    private ObjectNode toJson(Object instance) {
        try {
            if (wasSerialized(instance)) {
                return getPreviouslySerializedInstance(instance);
            }
            return trySerializeToJson(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectNode getPreviouslySerializedInstance(Object instance) {
        JsonNode visitedInstance = serializedInstances.get(instance);
        ObjectNode referenceNode = new ObjectNode(JsonNodeFactory.instance);
        referenceNode.set("type", new TextNode(REFERENCE_TO_OBJECT));
        referenceNode.set("value", new TextNode(visitedInstance.get(FIELD_ID).asText()));
        return referenceNode;
    }

    private boolean wasSerialized(Object instance) {
        return serializedInstances.containsKey(instance);
    }

    private ObjectNode trySerializeToJson(Object instance) throws Exception {
        ObjectNode rootNode = new ObjectNode(JsonNodeFactory.instance);
        rootNode.set("targetClass", new TextNode(instance.getClass().getName()));
        rootNode.set(FIELD_ID, new LongNode(fieldIdGenerator.incrementAndGet()));
        Class<?> instanceClass = instance.getClass();
        if (!wasSerialized(instance)) {
            serializedInstances.put(instance, rootNode);
        }
        rootNode.set("state", serializeFieldsOf(instance, instanceClass));
        return rootNode;
    }

    private ObjectNode serializeFieldsOf(Object instance, Class<?> instanceClass) throws Exception {
        ObjectNode stateNode = new ObjectNode(JsonNodeFactory.instance);
        boolean shouldQualifyFieldNames = shouldQualifyFieldNamesFor(instanceClass);
        do {
            processFieldsFor(instance, stateNode, instanceClass, shouldQualifyFieldNames);
        } while ((instanceClass = instanceClass.getSuperclass()) != null);
        return stateNode;
    }

    private void processFieldsFor(Object instance, ObjectNode stateNode, Class<?> instanceClass, boolean shouldQualifyFieldNames) throws Exception {
        for (Field field : instanceClass.getDeclaredFields()) {
            field.setAccessible(true);
            int fieldModifiers = field.getModifiers();
            if (Modifier.isStatic(fieldModifiers)) {
                continue;
            }
            stateNode.set(makeFieldName(field, shouldQualifyFieldNames), serializeField(field, instance));
        }
    }

    private JsonNode serializeField(Field field, Object instance) throws Exception {
        return objectSerializer.serialize(field.get(instance), field.getType());
    }

    private String makeFieldName(Field field, boolean shouldQualify) {
        return shouldQualify ? field.getDeclaringClass().getName() + "." + field.getName() : field.getName();
    }

    private boolean shouldQualifyFieldNamesFor(Class<?> clazz) {
        Set<String> fieldNames = new HashSet<>();
        do {
            for (Field field : clazz.getDeclaredFields()) {
                if (!fieldNames.add(field.getName())) {
                    return true;
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        return false;
    }

    public JsonNode serializeValue(Object instance) {
        for (SerializerDeserializer serde : serializerDeserializers) {
            if (serde.canConsume(instance != null ? instance.getClass() : null)) {
                return serde.serialize(instance);
            }
        }
        return toJson(instance);
    }

    public <T> T deserialize(String json, Class<T> clazz) {
        try {
            if (isNull(json)) {
                return null;
            }
            return tryDeserialize(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T tryDeserialize(String json) throws Exception {
        JsonNode rootNode = objectMapper.readTree(json);
        if (rootNode.has("type")) {
            return (T) getValueAs(rootNode.get("value"), rootNode.get("type").asText());
        }
        JsonNode stateNode = rootNode.get("state");
        String resultingClassName = rootNode.get("targetClass").asText();
        String fieldId = rootNode.get(FIELD_ID).asText();
        Class<?> resultingClass = Class.forName(resultingClassName);
        Object instance = objectFactory.makeInstance(resultingClass);
        deserializedInstances.put(fieldId, instance);
        Iterator<Map.Entry<String, JsonNode>> fields = stateNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> fieldEntry = fields.next();
            JsonNode fieldNode = fieldEntry.getValue();
            deserializeField(resultingClass, instance, fieldEntry, fieldNode);
        }
        return (T) instance;
    }

    private boolean isNull(String json) {
        return json == null || json.equals("null");
    }

    private void deserializeField(Class<?> resultingClass, Object instance, Map.Entry<String, JsonNode> fieldEntry, JsonNode fieldNode) throws Exception {
        Field field = getDeclaredField(fieldEntry.getKey(), resultingClass);
        if (field == null) {
            throw new FieldFieldException("Field " + fieldEntry.getKey() + " was not found present in class " + resultingClass.getName());
        }
        field.setAccessible(true);
        Object nodeValue = getNodeValue(fieldNode);
        field.set(instance, castValueTo(nodeValue, field.getType()));
    }

    private Field getDeclaredField(String fieldName, Class<?> clazz) {
        try {
            return tryGetDeclaredField(fieldName, clazz);
        } catch (Exception e) {
            return getDeclaredField(fieldName, clazz.getSuperclass());
        }
    }

    private Field tryGetDeclaredField(String fieldName, Class<?> clazz) throws Exception {
        if (clazz == null) {
            return null;
        } else if (isQualifiedField(fieldName)) {
            return getQualifiedField(fieldName);
        }
        return clazz.getDeclaredField(fieldName);
    }

    private Field getQualifiedField(String qualifiedFieldName) throws Exception {
        String className = qualifiedFieldName.substring(0, qualifiedFieldName.lastIndexOf("."));
        String fieldName = qualifiedFieldName.substring(qualifiedFieldName.lastIndexOf(".") + 1);
        Class<?> fieldClass = Class.forName(className);
        return fieldClass.getDeclaredField(fieldName);
    }

    private boolean isQualifiedField(String fieldName) {
        return fieldName.contains(".");
    }

    private Object castValueTo(Object instance, Class<?> targetType) {
        ValueCastUtil castUtil = new ValueCastUtil();
        return castUtil.castValueTo(instance, targetType);
    }

    public Object getNodeValue(JsonNode fieldNode) throws Exception {
        for (SerializerDeserializer serde : serializerDeserializers) {
            if (serde.canConsume(fieldNode)) {
                return serde.deserialize(fieldNode);
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
                    return serde.deserialize(resultingClass, value);
                }
                if (serde.canConsume(value)) {
                    return serde.deserialize(resultingClass, value);
                }
            }
        }
        return null;
    }

    public static class FieldFieldException extends RuntimeException {
        public FieldFieldException(String message) {
            super(message);
        }
    }
}
