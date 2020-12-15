package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerializerDeserializer;
import inc.evil.serde.util.ValueCastUtil;

public class NumericSerde implements SerializerDeserializer {
    private static final Class<?>[] NUMERIC_WRAPPER_TYPES = {
            Byte.class,
            Character.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class
    };

    private final SerdeContext serdeContext;

    public NumericSerde(SerdeContext serdeContext) {
        this.serdeContext = serdeContext;
    }

    @Override
    public JsonNode serialize(Object instance) {
        if (instance instanceof Double) {
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

    @Override
    public boolean canConsume(Class<?> clazz) {
        return hasSuperclass(clazz, Character.class) || isPrimitiveOrWrapper(clazz);
    }

    @Override
    public boolean canConsume(JsonNode node) {
        return node.isFloat() || node.isDouble() || node.isNumber();
    }

    private boolean hasSuperclass(Class<?> clazz, Class<?> searchedSuperclass) {
        Class<?> currentClass = clazz;
        do {
            if (currentClass == searchedSuperclass) {
                return true;
            }
        } while ((currentClass = currentClass.getSuperclass()) != null);
        return false;
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        for (Class<?> wrapperType : NUMERIC_WRAPPER_TYPES) {
            if (clazz == wrapperType) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object deserialize(Class<?> resultingClass, JsonNode node) throws Exception {
        ValueCastUtil valueCastUtil = new ValueCastUtil();
        return valueCastUtil.castValueTo(serdeContext.getNodeValue(node), resultingClass);
    }

    @Override
    public Object deserialize(JsonNode node) throws Exception {
        if (node.isFloat() || node.isDouble()) {
            return node.asDouble();
        } else if (node.isNumber()) {
            return node.asLong();
        } else {
            throw new RuntimeException(node.toPrettyString() + " is not numeric");
        }
    }
}
