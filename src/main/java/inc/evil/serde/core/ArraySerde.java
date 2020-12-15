package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerializerDeserializer;
import inc.evil.serde.util.ValueCastUtil;

import java.lang.reflect.Array;
import java.util.List;

public class ArraySerde implements SerializerDeserializer {
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

    private final SerdeContext serdeContext;
    private final List<SerializerDeserializer> delegates;
    private final ValueCastUtil valueCastUtil = new ValueCastUtil();

    public ArraySerde(SerdeContext serdeContext, List<SerializerDeserializer> delegates) {
        this.serdeContext = serdeContext;
        this.delegates = delegates;
    }

    @Override
    public JsonNode serialize(Object array) {
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
            return serdeContext.serializeValue(currentItem);
        } else if (currentItem != null && (!isWrapperOf(currentItem.getClass(), componentType))) {
            ObjectNode itemNode = new ObjectNode(JsonNodeFactory.instance);
            itemNode.set("type", new TextNode(currentItem.getClass().getName()));
            itemNode.set("value", serdeContext.serializeValue(currentItem));
            return itemNode;
        } else {
            return serdeContext.serializeValue(currentItem);
        }
    }

    private boolean isPrimitiveArray(Class<?> componentType) {
        Class<?> currentComponentType = componentType;
        while (currentComponentType.isArray()) {
            currentComponentType = currentComponentType.getComponentType();
        }
        return isPrimitiveOrWrapper(currentComponentType);
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        for (Class<?>[] wrapperType : WRAPPER_TYPES) {
            if (clazz == wrapperType[0] || clazz == wrapperType[1]) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return clazz.isArray();
    }

    @Override
    public boolean canConsume(JsonNode node) {
        return node.isArray();
    }

    @Override
    public Object deserialize(JsonNode node) throws Exception {
        for (SerializerDeserializer serde : delegates) {
            if (serde.canConsume(node)) {
                return serde.deserialize(node);
            }
        }
        return null;
    }

    @Override
    public Object deserialize(Class<?> resultingClass, JsonNode node) throws Exception {
        for (SerializerDeserializer serde : delegates) {
            if (serde.canConsume(resultingClass)) {
                return serde.deserialize(resultingClass, node);
            }
        }
        ArrayNode arrayNode = (ArrayNode) node;
        Object resultingArray = Array.newInstance(resultingClass.getComponentType(), arrayNode.size());
        int length = arrayNode.size();
        Class<?> componentType = resultingClass.getComponentType();
        for (int i = 0; i < length; ++i) {
            JsonNode currentNode = arrayNode.get(i);
            Object value = currentNode.isObject() ? serdeContext.deserialize(currentNode.toString(), componentType) : serdeContext.getNodeValue(currentNode);
            Array.set(resultingArray, i, shouldCastArrayElement(componentType, value) ? valueCastUtil.castValueTo(value, componentType) : value);
        }
        return resultingArray;
    }

    private boolean shouldCastArrayElement(Class<?> componentType, Object value) {
        return componentType.isPrimitive() || (value != null && isWrapperOf(value.getClass(), componentType)) ||
               (value != null && componentType != value.getClass());
    }

    private boolean isWrapperOf(Class<?> wrapperType, Class<?> checkedType) {
        for (Class<?>[] wrapper : WRAPPER_TYPES) {
            if (wrapperType == wrapper[0] && checkedType == wrapper[1]) {
                return true;
            }
        }
        return false;
    }
}
