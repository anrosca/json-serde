package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerializerDeserializer;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigNumbersSerde implements SerializerDeserializer {
    @Override
    public JsonNode serialize(Object instance, SerdeContext serdeContext) {
        if (instance instanceof BigDecimal) {
            return new TextNode(instance.toString());
        } else if (instance instanceof BigInteger) {
            return new TextNode((instance.toString()));
        }
        throw new RuntimeException("Alarm");
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return clazz == BigInteger.class || clazz == BigDecimal.class;
    }

    @Override
    public Object deserialize(Class<?> resultingClass, JsonNode node, SerdeContext serdeContext) throws Exception {
        if (resultingClass == BigInteger.class) {
            return new BigInteger(node.asText());
        } else if (resultingClass == BigDecimal.class) {
            return new BigDecimal(node.asText());
        }
        throw new IllegalStateException(resultingClass.getName() + " can't be deserialized by " + getClass().getCanonicalName());
    }
}
