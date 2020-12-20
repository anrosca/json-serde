package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.ObjectFactory;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerializerDeserializer;
import inc.evil.serde.cast.PrimitiveTypeCaster;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class ObjectSerde implements SerializerDeserializer {
    private static final String REFERENCE_TO_OBJECT = "__ref";
    private static final String FIELD_ID = "__id";

    private final ObjectFactory objectFactory = new ObjectFactory();
    private final PrimitiveTypeCaster primitiveTypeCaster = new PrimitiveTypeCaster();

    @Override
    public JsonNode serialize(Object instance, SerdeContext serdeContext) {
        try {
            return trySerialize(instance, serdeContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode trySerialize(Object instance, SerdeContext serdeContext) throws Exception {
        if (serdeContext.wasSerialized(instance)) {
            return serdeContext.getPreviouslySerializedInstance(instance);
        }
        ObjectNode rootNode = new ObjectNode(JsonNodeFactory.instance);
        serdeContext.addSerializedInstance(instance, rootNode);
        rootNode.set("targetClass", new TextNode(instance.getClass().getName()));
        rootNode.set(FIELD_ID, new LongNode(serdeContext.generateObjectId()));
        rootNode.set("state", serializeFieldsOf(instance, serdeContext));
        return rootNode;
    }

    private JsonNode serializeFieldsOf(Object instance, SerdeContext serdeContext) throws Exception {
        boolean shouldQualifyFieldNames = shouldQualifyFieldNamesFor(instance.getClass());
        ObjectNode stateNode = new ObjectNode(JsonNodeFactory.instance);
        for (Field field : getDeclaredFieldsOf(instance)) {
            field.setAccessible(true);
            if (shouldSerializeField(field)) {
                stateNode.set(makeFieldName(field, shouldQualifyFieldNames), serdeContext.serializeValue(field.get(instance)));
            }
        }
        return stateNode;
    }

    private boolean shouldSerializeField(Field field) {
        int fieldModifiers = field.getModifiers();
        return !Modifier.isStatic(fieldModifiers);
    }

    private List<Field> getDeclaredFieldsOf(Object instance) {
        Class<?> currentClass = instance.getClass();
        List<Field> fields = new ArrayList<>();
        do {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
        } while ((currentClass = currentClass.getSuperclass()) != null);
        return fields;
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

    @Override
    public JsonNode serialize(Object instance, Class<?> type, SerdeContext serdeContext) {
        ObjectNode fieldNode = new ObjectNode(JsonNodeFactory.instance);
        fieldNode.set("type", new TextNode(instance != null ? instance.getClass().getName() : type.getName()));
        fieldNode.set("value", serdeContext.serializeValue(instance));
        return fieldNode;
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return true;
    }

    @Override
    public boolean canConsume(JsonNode node) {
        return node.isObject() && (node.has("type") || node.has("targetClass"));
    }

    @Override
    public Object deserialize(Class<?> resultingClass, JsonNode node, SerdeContext serdeContext) throws Exception {
        return serdeContext.deserialize(node.toString(), resultingClass);
    }

    @Override
    public Object deserialize(JsonNode node, SerdeContext serdeContext) throws Exception {
        if (node.has("type")) {
            String className = node.get("type").asText();
            if (className.equals(REFERENCE_TO_OBJECT)) {
                return serdeContext.getPreviouslyDeserializedInstance(node.get("value").asText());
            }
            Class<?> resultingClass = Class.forName(className);
            return serdeContext.deserialize(node.toString(), resultingClass);
        } else if (node.has("targetClass")) {
            String className = node.get("targetClass").asText();
            if (className != null) {
                Class<?> resultingClass = Class.forName(className);
                return tryDeserialize(node.toString(), resultingClass, serdeContext);
            }
        }
        throw new IllegalStateException("Could not deserialize node because of missing type information. Json node: " + node);
    }

    private <T> T tryDeserialize(String json, Class<T> resultingType, SerdeContext serdeContext) throws Exception {
        JsonNode rootNode = new ObjectMapper().readTree(json);
        JsonNode stateNode = rootNode.get("state");
        String resultingClassName = rootNode.get("targetClass").asText();
        String fieldId = rootNode.get(FIELD_ID).asText();
        Class<?> resultingClass = Class.forName(resultingClassName);
        Object instance = objectFactory.makeInstance(resultingClass);
        serdeContext.addDeserializedInstance(fieldId, instance);
        Iterator<Map.Entry<String, JsonNode>> fields = stateNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> fieldEntry = fields.next();
            JsonNode fieldNode = fieldEntry.getValue();
            deserializeField(instance, fieldEntry.getKey(), fieldNode, serdeContext);
        }
        return primitiveTypeCaster.castValueTo(instance, resultingType);
    }

    private void deserializeField(Object instance, String fieldName, JsonNode fieldNode, SerdeContext serdeContext) throws Exception {
        Field field = getDeclaredField(fieldName, instance.getClass());
        if (field == null) {
            throw new IllegalStateException("Field " + fieldName + " was not found present in class " +
                    instance.getClass().getName());
        }
        field.setAccessible(true);
        Object nodeValue = serdeContext.deserializeValue(fieldNode);
        field.set(instance, primitiveTypeCaster.castValueTo(nodeValue, field.getType()));
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
}
