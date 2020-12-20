package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerializerDeserializer;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigNumbersSerde implements SerializerDeserializer {
    @Override
    public JsonNode serialize(Object instance, SerdeContext serdeContext) {
        ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
        objectNode.set("type", new TextNode(instance.getClass().getName()));
        objectNode.set("value", new TextNode(instance.toString()));
        return objectNode;
    }

    @Override
    public boolean canConsume(JsonNode node) {
        return node.has("type") &&
                (node.get("type").asText().equals(BigInteger.class.getName()) || node.get("type").asText().equals(BigDecimal.class.getName()));
    }

    @Override
    public Object deserialize(JsonNode node, SerdeContext serdeContext) throws Exception {
        String className = node.get("type").asText();
        JsonNode value = node.get("value");
        return deserialize(Class.forName(className), value, serdeContext);
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
