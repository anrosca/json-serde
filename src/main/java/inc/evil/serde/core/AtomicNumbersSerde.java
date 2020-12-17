package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerializerDeserializer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicNumbersSerde implements SerializerDeserializer {
    @Override
    public JsonNode serialize(Object instance, SerdeContext serdeContext) {
        Number number = (Number) instance;
        return new LongNode(number.longValue());
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return clazz == AtomicInteger.class || clazz == AtomicLong.class;
    }

    @Override
    public Object deserialize(Class<?> resultingClass, JsonNode node, SerdeContext serdeContext) throws Exception {
        Number number = node.asLong();
        if (resultingClass == AtomicInteger.class) {
            return new AtomicInteger(number.intValue());
        } else if (resultingClass == AtomicLong.class) {
            return new AtomicLong(number.longValue());
        }
        throw new IllegalStateException(resultingClass.getName() + " can't be deserialized by " + getClass().getCanonicalName());
    }
}
