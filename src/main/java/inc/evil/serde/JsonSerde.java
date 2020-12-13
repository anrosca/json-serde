package inc.evil.serde;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

class JsonSerde {
    private static final String REFERENCE_TO_OBJECT = "__ref";
    private static final String FIELD_ID = "__id";
    private static final Class<?>[][] WRAPPER_TYPES = {
            {Boolean.class, boolean.class},
            {Byte.class, byte.class},
            {Character.class, char.class},
            {Short.class, short.class},
            {Integer.class, int.class},
            {Long.class, long.class},
            {Float.class, float.class},
            {Double.class, double.class},
    };
    private static final Map<Class<?>, Function<Object, Object>> castingFunction = new HashMap<>();

    static {
        castingFunction.put(boolean.class, (value) -> (boolean) value);
        castingFunction.put(Boolean.class, (value) -> (boolean) value);
        castingFunction.put(byte.class, (value) -> ((Number) value).byteValue());
        castingFunction.put(Byte.class, (value) -> ((Number) value).byteValue());
        castingFunction.put(char.class, (value) -> (char) (((Number) value).intValue()));
        castingFunction.put(Character.class, (value) -> (char) (((Number) value).intValue()));
        castingFunction.put(short.class, (value) -> ((Number) value).shortValue());
        castingFunction.put(Short.class, (value) -> ((Number) value).shortValue());
        castingFunction.put(int.class, (value) -> ((Number) value).intValue());
        castingFunction.put(Integer.class, (value) -> ((Number) value).intValue());
        castingFunction.put(long.class, (value) -> ((Number) value).longValue());
        castingFunction.put(Long.class, (value) -> ((Number) value).longValue());
        castingFunction.put(float.class, (value) -> ((Number) value).floatValue());
        castingFunction.put(Float.class, (value) -> ((Number) value).floatValue());
        castingFunction.put(double.class, (value) -> ((Number) value).doubleValue());
        castingFunction.put(Double.class, (value) -> ((Number) value).doubleValue());
        castingFunction.put(AtomicInteger.class, (value) -> new AtomicInteger(((Number) value).intValue()));
        castingFunction.put(AtomicLong.class, (value) -> new AtomicLong(((Number) value).longValue()));
        castingFunction.put(AtomicBoolean.class, (value) -> new AtomicBoolean(((Boolean) value)));
        castingFunction.put(BigDecimal.class, (value) -> new BigDecimal(value.toString()));
        castingFunction.put(BigInteger.class, (value) -> new BigInteger(value.toString()));
    }

    private final Map<Object, JsonNode> serializedInstances = new IdentityHashMap<>();
    private final Map<String, Object> deserializedInstances = new HashMap<>();
    private final AtomicLong fieldIdGenerator = new AtomicLong();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectFactory objectFactory = new ObjectFactory();

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
        ObjectNode stateNode = new ObjectNode(JsonNodeFactory.instance);
        rootNode.set("targetClass", new TextNode(instance.getClass().getName()));
        rootNode.set("state", stateNode);
        rootNode.set(FIELD_ID, new LongNode(fieldIdGenerator.incrementAndGet()));
        Class<?> instanceClass = instance.getClass();
        if (!wasSerialized(instance)) {
            serializedInstances.put(instance, rootNode);
        }
        boolean shouldQualifyFieldNames = shouldQualifyFieldNamesFor(instanceClass);
        do {
            for (Field field : instanceClass.getDeclaredFields()) {
                field.setAccessible(true);
                int fieldModifiers = field.getModifiers();
                if (Modifier.isStatic(fieldModifiers)) {
                    continue;
                }
                stateNode.set(makeFieldName(field, shouldQualifyFieldNames), serializeField(field, instance));
            }
        } while ((instanceClass = instanceClass.getSuperclass()) != null);
        return rootNode;
    }

    private JsonNode serializeField(Field field, Object instance) throws Exception {
        if (isPrimitive(field.getType())) {
            return serializeValue(field.get(instance));
        } else if (field.getType().isArray()) {
            return serializeArray(field.get(instance));
        } else {
            return serializeObject(instance, field);
        }
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

    private ObjectNode serializeObject(Object instance, Field field) throws IllegalAccessException {
        ObjectNode fieldNode = new ObjectNode(JsonNodeFactory.instance);
        Object fieldValue = field.get(instance);
        fieldNode.set("type", new TextNode(fieldValue != null ? fieldValue.getClass().getName() : field.getType().getName()));
        fieldNode.set("value", serializeValue(field.get(instance)));
        return fieldNode;
    }

    private JsonNode serializeArray(Object array) {
        if (array == null) {
            return NullNode.getInstance();
        }
        ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
        objectNode.set("type", new TextNode(array.getClass().getName()));
        ArrayNode jsonNodes = new ArrayNode(JsonNodeFactory.instance);
        objectNode.set("value", jsonNodes);
        Class<?> componentType = array.getClass().getComponentType();
        for (int i = 0; i < Array.getLength(array); ++i) {
            Object currentItem = Array.get(array, i);
            jsonNodes.add(serializeArrayItem(currentItem, componentType));
        }
        return objectNode;
    }

    private JsonNode serializeArrayItem(Object currentItem, Class<?> componentType) {
        if (isPrimitiveArray(componentType)) {
            return serializeValue(currentItem);
        } else if (currentItem != null && (!isWrapperOf(currentItem.getClass(), componentType))) {
            ObjectNode itemNode = new ObjectNode(JsonNodeFactory.instance);
            itemNode.set("type", new TextNode(currentItem.getClass().getName()));
            itemNode.set("value", serializeValue(currentItem));
            return itemNode;
        } else {
            return serializeValue(currentItem);
        }
    }

    private boolean isPrimitiveArray(Class<?> componentType) {
        Class<?> currentComponentType = componentType;
        while (currentComponentType.isArray()) {
            currentComponentType = currentComponentType.getComponentType();
        }
        return isPrimitiveOrWrapper(currentComponentType);
    }

    private boolean isWrapperOf(Class<?> wrapperType, Class<?> checkedType) {
        for (Class<?>[] wrapper : WRAPPER_TYPES) {
            if (wrapperType == wrapper[0] && checkedType == wrapper[1]) {
                return true;
            }
        }
        return false;
    }

    private boolean isPrimitive(Class<?> type) {
        return type.isPrimitive();
    }

    private boolean isCollection(Class<?> type) {
        return type == ArrayList.class || type == HashSet.class || type == LinkedList.class;
    }

    @SuppressWarnings("unchecked")
    private JsonNode serializeValue(Object instance) {
        if (instance == null) {
            return NullNode.getInstance();
        } else if (instance instanceof String) {
            return new TextNode((String) instance);
        } else if (isNumeric(instance)) {
            return serializeNumericValue(instance);
        } else if (instance instanceof AtomicBoolean) {
            return BooleanNode.valueOf(((AtomicBoolean) instance).get());
        } else if (instance instanceof Boolean) {
            return BooleanNode.valueOf((Boolean) instance);
        } else if (isCollection(instance.getClass())) {
            return serializeCollection((Collection<Object>) instance);
        } else if (isMap(instance.getClass())) {
            return serializeMap((Map<Object, Object>) instance);
        } else if (instance.getClass().isEnum()) {
            return new TextNode(instance.toString());
        } else if (instance.getClass().isArray()) {
            return serializeArray(instance);
        } else if (instance instanceof Class) {
            return new TextNode(((Class<?>) instance).getName());
        } else if (isDate(instance.getClass())) {
            return new TextNode(instance.toString());
        } else {
            return toJson(instance);
        }
    }

    private JsonNode serializeMap(Map<Object, Object> instance) {
        ArrayNode jsonNodes = new ArrayNode(JsonNodeFactory.instance);
        for (Map.Entry<Object, Object> entry : instance.entrySet()) {
            ObjectNode mapEntryNode = new ObjectNode(JsonNodeFactory.instance);
            mapEntryNode.set("key", serializeValue(entry.getKey()));
            mapEntryNode.set("value", serializeValue(entry.getValue()));
            jsonNodes.add(mapEntryNode);
        }
        return jsonNodes;
    }

    private boolean isMap(Class<?> clazz) {
        return clazz == HashMap.class || clazz == ConcurrentHashMap.class || clazz == TreeMap.class;
    }

    private boolean isDate(Class<?> clazz) {
        return clazz == LocalDateTime.class || clazz == LocalDate.class ||
               clazz == OffsetDateTime.class || clazz == ZonedDateTime.class ||
               clazz == Period.class || clazz == Duration.class;
    }

    private ArrayNode serializeCollection(Collection<Object> instance) {
        ArrayNode jsonNodes = new ArrayNode(JsonNodeFactory.instance);
        for (Object item : instance) {
            jsonNodes.add(serializeValue(item));
        }
        return jsonNodes;
    }

    private JsonNode serializeNumericValue(Object instance) {
        if (instance instanceof BigDecimal) {
            return new TextNode(instance.toString());
        } else if (instance instanceof BigInteger) {
            return new TextNode((instance.toString()));
        } else if (instance instanceof Double) {
            return new DoubleNode((Double) instance);
        } else if (instance instanceof Float) {
            return new FloatNode((Float) instance);
        } else if (instance instanceof Long) {
            return new LongNode((Long) instance);
        } else if (instance instanceof Character) {
            return new IntNode((Character) instance);
        } else {
            Number number = (Number) instance;
            return new IntNode(number.intValue());
        }
    }

    private boolean isNumeric(Object instance) {
        return instance instanceof Number || instance instanceof Character;
    }

    @SuppressWarnings("unchecked")
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

    private Object castValueTo(Object nodeValue, Class<?> targetType) {
        if (nodeValue == null)
            return null;
        return castingFunction.getOrDefault(targetType, (value) -> value).apply(nodeValue);
    }

    private Object getNodeValue(JsonNode fieldNode) throws Exception {
        if (fieldNode.isBoolean()) {
            return fieldNode.asBoolean();
        } else if (fieldNode.isFloat() || fieldNode.isDouble()) {
            return fieldNode.asDouble();
        } else if (fieldNode.isNumber()) {
            return fieldNode.asLong();
        } else if (fieldNode.isTextual()) {
            return fieldNode.asText();
        } else if (fieldNode.isNull()) {
            return null;
        } else {
            JsonNode value = fieldNode.get("value");
            JsonNode type = fieldNode.get("type");
            return getValueAs(value, type.asText());
        }
    }

    private Object getValueAs(JsonNode value, String type) throws Exception {
        if (value == null || value.asText().equals("null")) {
            return null;
        } else if (type != null) {
            if (type.equals(REFERENCE_TO_OBJECT)) {
                return deserializedInstances.get(value.asText());
            }
            Class<?> resultingClass = Class.forName(type);
            if (resultingClass == String.class) {
                return value.asText().equals("null") ? null : value.asText();
            } else if (resultingClass == Class.class) {
                return Class.forName(value.asText());
            } else if (resultingClass.isEnum()) {
                return getEnumValue(resultingClass, value.asText());
            } else if (value.isObject()) {
                return deserialize(value.toString(), resultingClass);
            } else if (resultingClass.isArray()) {
                return deserializeArray(resultingClass, (ArrayNode) value);
            } else if (isDate(resultingClass)) {
                return deserializeDate(resultingClass, value.asText());
            } else if (value.isNumber() || isBigNumber(resultingClass) || isPrimitiveOrWrapper(resultingClass)) {
                return castValueTo(getNodeValue(value), resultingClass);
            } else if (value.isBoolean()) {
                return getNodeValue(value);
            } else if (value.isArray()) {
                return deserializeCollection(resultingClass, (ArrayNode) value);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Object deserializeCollection(Class<?> resultingClass, ArrayNode arrayNode) throws Exception {
        if (isCollection(resultingClass)) {
            return deserializeCommonCollection(resultingClass, arrayNode);
        } else if (isMap(resultingClass)) {
            return deserializeMap(resultingClass, arrayNode);
        } else {
            return /*deserializeArray(resultingClass, arrayNode)*/ null;
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<Object> deserializeCommonCollection(Class<?> resultingClass, ArrayNode arrayNode) throws Exception {
        Collection<Object> collection = (Collection<Object>) objectFactory.makeInstance(resultingClass);
        for (int i = 0; i < arrayNode.size(); ++i) {
            JsonNode currentNode = arrayNode.get(i);
            Object value = currentNode.isObject() ? deserialize(currentNode.toString(), resultingClass) : getNodeValue(currentNode);
            collection.add(value);
        }
        return collection;
    }

    @SuppressWarnings("unchecked")
    private Map<Object, Object> deserializeMap(Class<?> resultingClass, ArrayNode arrayNode) throws Exception {
        Map<Object, Object> map = (Map<Object, Object>) objectFactory.makeInstance(resultingClass);
        for (int i = 0; i < arrayNode.size(); ++i) {
            JsonNode currentNode = arrayNode.get(i);
            JsonNode keyNode = currentNode.get("key");
            JsonNode valueNode = currentNode.get("value");
            Object key = keyNode.isObject() ? deserialize(keyNode.toString(), resultingClass) : getNodeValue(keyNode);
            Object value = valueNode.isObject() ? deserialize(valueNode.toString(), resultingClass) : getNodeValue(valueNode);
            map.put(key, value);
        }
        return map;
    }

    private boolean isBigNumber(Class<?> resultingClass) {
        return resultingClass == BigDecimal.class || resultingClass == BigInteger.class;
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        for (Class<?>[] wrapperType : WRAPPER_TYPES) {
            if (clazz == wrapperType[0] || clazz == wrapperType[1]) {
                return true;
            }
        }
        return false;
    }

    private Object deserializeDate(Class<?> resultingClass, String value) throws Exception {
        if (value == null || value.equals("null")) {
            return null;
        }
        Method parseMethod = resultingClass.getDeclaredMethod("parse", CharSequence.class);
        parseMethod.setAccessible(true);
        return parseMethod.invoke(null, value);
    }

    private Object deserializeArray(Class<?> resultingClass, ArrayNode arrayNode) throws Exception {
        Object resultingArray = Array.newInstance(resultingClass.getComponentType(), arrayNode.size());
        int length = arrayNode.size();
        Class<?> componentType = resultingClass.getComponentType();
        for (int i = 0; i < length; ++i) {
            JsonNode currentNode = arrayNode.get(i);
            Object value = currentNode.isObject() ? deserialize(currentNode.toString(), componentType) : getNodeValue(currentNode);
            Array.set(resultingArray, i, shouldCastArrayElement(componentType, value) ? castValueTo(value, componentType) : value);
        }
        return resultingArray;
    }

    private boolean shouldCastArrayElement(Class<?> componentType, Object value) {
        return componentType.isPrimitive() || (value != null && isWrapperOf(value.getClass(), componentType)) ||
               (value != null && componentType != value.getClass());
    }

    private Enum<?> getEnumValue(Class<?> enumClass, String enumValue) throws Exception {
        if (enumValue == null || enumValue.equals("null")) {
            return null;
        }
        Method valueOfMethod = enumClass.getDeclaredMethod("valueOf", String.class);
        valueOfMethod.setAccessible(true);
        return (Enum<?>) valueOfMethod.invoke(null, enumValue);
    }

    public static class FieldFieldException extends RuntimeException {
        public FieldFieldException(String message) {
            super(message);
        }
    }
}
