package inc.evil.serde;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class JsonSerde {

    private final Map<Object, JsonNode> serializedInstances = new IdentityHashMap<>();
    private final Map<String, Object> deserializedInstances = new HashMap<>();
    private final AtomicLong fieldIdGenerator = new AtomicLong();
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        referenceNode.set("type", new TextNode("__ref"));
        referenceNode.set("value", new TextNode(visitedInstance.get("__idRef").asText()));
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
        rootNode.set("__idRef", new LongNode(fieldIdGenerator.incrementAndGet()));
        Class<?> instanceClass = instance.getClass();
        if (!wasSerialized(instance)) {
            serializedInstances.put(instance, rootNode);
        }
        do {
            for (Field field : instanceClass.getDeclaredFields()) {
                field.setAccessible(true);
                int fieldModifiers = field.getModifiers();
                if (isSynthetic(fieldModifiers) || Modifier.isStatic(fieldModifiers) /*|| Modifier.isTransient(fieldModifiers)*/) {
                    continue;
                }
                if (isCollection(field.getType()) || isPrimitive(field.getType())) {
                    stateNode.set(field.getName(), serializeValue(field.get(instance)));
                } else if (field.getType().isArray()) {
                    stateNode.set(field.getName(), serializeArray(field.get(instance)));
                } else {
                    serializeObject(instance, stateNode, field);
                }
            }
        } while ((instanceClass = instanceClass.getSuperclass()) != null);
        return rootNode;
    }

    private void serializeObject(Object instance, ObjectNode stateNode, Field field) throws IllegalAccessException {
        ObjectNode fieldNode = new ObjectNode(JsonNodeFactory.instance);
        stateNode.set(field.getName(), fieldNode);
        Object fieldValue = field.get(instance);
        fieldNode.set("type", new TextNode(fieldValue != null ? fieldValue.getClass().getName() : field.getType().getName()));
        fieldNode.set("value", serializeValue(field.get(instance)));
    }

    private boolean isSynthetic(int fieldModifiers) throws Exception {
        Method isSynthetic = Modifier.class.getDeclaredMethod("isSynthetic", int.class);
        isSynthetic.setAccessible(true);
        return (boolean) isSynthetic.invoke(null, fieldModifiers);
    }

    private JsonNode serializeArray(Object array) {
        if (array == null) {
            return NullNode.getInstance();
        }
        ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
        objectNode.set("type", new TextNode(array.getClass().getName()));
        ArrayNode jsonNodes = new ArrayNode(JsonNodeFactory.instance);
        objectNode.set("value", jsonNodes);
        for (int i = 0; i < Array.getLength(array); ++i) {
            Object currentItem = Array.get(array, i);
            jsonNodes.add(serializeValue(currentItem));
        }
        return objectNode;
    }

    private boolean isPrimitive(Class<?> type) {
        return type.isPrimitive();
    }

    private boolean isCollection(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    @SuppressWarnings("unchecked")
    private JsonNode serializeValue(Object instance) {
        if (instance == null) {
            return NullNode.getInstance();
        } else if (instance instanceof String) {
            return new TextNode((String) instance);
        } else if (isNumeric(instance)) {
            return serializeNumericValue(instance);
        } else if (instance instanceof Boolean) {
            return BooleanNode.valueOf((Boolean) instance);
        } else if (instance instanceof Collection) {
            return serializeCollection((Collection<Object>) instance);
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
            return tryDeserialize(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T tryDeserialize(String json) throws Exception {
        if (json.equals("null")) {
            return null;
        }
        JsonNode rootNode = objectMapper.readTree(json);
        if (rootNode.get("type") != null) {
            return (T) getValueAs(rootNode.get("value"), rootNode.get("type").asText());
        }
        JsonNode stateNode = rootNode.get("state");
        String resultingType = rootNode.get("targetClass").asText();
        String fieldId = rootNode.get("__idRef").asText();
        Class<?> resultingClass = Class.forName(resultingType);
        Object instance = makeInstance(resultingClass);
        deserializedInstances.put(fieldId, instance);
        Iterator<Map.Entry<String, JsonNode>> fields = stateNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> fieldEntry = fields.next();
            JsonNode fieldNode = fieldEntry.getValue();
            Field field = getDeclaredField(fieldEntry.getKey(), resultingClass);
            if (field == null) {
                throw new FieldFieldException("Field " + fieldEntry.getKey() + " was not found present in class " + resultingClass.getName());
                //continue;
            }
            field.setAccessible(true);
            if (fieldNode.isArray()) {
                field.set(instance, getCollection((ArrayNode) fieldNode, field.getType()));
            } else {
                Object nodeValue = getNodeValue(fieldNode);
                field.set(instance, castValueTo(nodeValue, field.getType()));
            }
        }
        return (T) instance;
    }

    private Field getDeclaredField(String fieldName, Class<?> clazz) {
        try {
            if (clazz == null)
                return null;
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return getDeclaredField(fieldName, clazz.getSuperclass());
        }
    }

    private Object castValueTo(Object nodeValue, Class<?> targetType) {
        if (nodeValue == null)
            return null;
        if (targetType == boolean.class || targetType == Boolean.class) {
            return (boolean) nodeValue;
        } else if (targetType == byte.class || targetType == Byte.class) {
            return ((Number) nodeValue).byteValue();
        } else if (targetType == char.class || targetType == Character.class) {
            return (char) (((Number) nodeValue).intValue());
        } else if (targetType == short.class || targetType == Short.class) {
            return ((Number) nodeValue).shortValue();
        } else if (targetType == int.class || targetType == Integer.class) {
            return ((Number) nodeValue).intValue();
        } else if (targetType == long.class || targetType == Long.class) {
            return ((Number) nodeValue).longValue();
        } else if (targetType == float.class || targetType == Float.class) {
            return ((Number) nodeValue).floatValue();
        } else if (targetType == double.class || targetType == Double.class) {
            return ((Number) nodeValue).doubleValue();
        } else if (targetType == AtomicInteger.class) {
            return new AtomicInteger(((Number) nodeValue).intValue());
        } else if (targetType == AtomicLong.class) {
            return new AtomicLong(((Number) nodeValue).longValue());
        } else if (targetType == BigDecimal.class) {
            return new BigDecimal(nodeValue.toString());
        } else if (targetType == BigInteger.class) {
            return new BigInteger(nodeValue.toString());
        }
        return nodeValue;
    }

    private Object makeInstance(Class<?> clazz) {
        Objenesis objenesis = new ObjenesisStd();
        return objenesis.newInstance(clazz);
    }

    private Object getCollection(ArrayNode arrayNode, Class<?> type) throws Exception {
        if (List.class.isAssignableFrom(type)) {
            List<Object> resultingList = new ArrayList<>();
            for (int i = 0; i < arrayNode.size(); ++i) {
                JsonNode node = arrayNode.get(i);
                resultingList.add(getValueAs(node, null));
            }
            return resultingList;
        }
        return null;
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
        if (type != null) {
            if (type.equals("__ref")) {
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
            }
        }
        if (value.isTextual()) {
            return value.asText();
        }
        if (value.isNumber()) {
            return getNodeValue(value);
        }
        return null;
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
            Array.set(resultingArray, i, componentType.isPrimitive() ? castValueTo(value, componentType) : value);
        }
        return resultingArray;
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
