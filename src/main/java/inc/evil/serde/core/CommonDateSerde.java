package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerializerDeserializer;

import java.lang.reflect.Method;
import java.time.*;
import java.util.Arrays;

public class CommonDateSerde implements SerializerDeserializer {
    private static final Class<?>[] SUPPORTED_DATE_TYPES = {
            LocalDateTime.class,
            LocalDate.class,
            OffsetDateTime.class,
            ZonedDateTime.class,
            Instant.class,
            Period.class,
            Duration.class
    };

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
                Arrays.stream(SUPPORTED_DATE_TYPES)
                        .map(Class::getName)
                        .anyMatch(dateType -> dateType.equals(node.get("type").asText()));
    }

    @Override
    public Object deserialize(JsonNode node, SerdeContext serdeContext) throws Exception {
        return deserialize(Class.forName(node.get("type").asText()), node.get("value"), serdeContext);
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return Arrays.stream(SUPPORTED_DATE_TYPES)
                .anyMatch(supportedClass -> supportedClass == clazz);
    }

    @Override
    public Object deserialize(Class<?> resultingClass, JsonNode node, SerdeContext serdeContext) throws Exception {
        String value = node.asText();
        if (value == null || value.equals("null")) {
            return null;
        }
        Method parseMethod = resultingClass.getDeclaredMethod("parse", CharSequence.class);
        parseMethod.setAccessible(true);
        return parseMethod.invoke(null, value);
    }
}
