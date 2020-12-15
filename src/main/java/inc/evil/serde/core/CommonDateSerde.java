package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
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
    public JsonNode serialize(Object instance) {
        return new TextNode(instance.toString());
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return Arrays.stream(SUPPORTED_DATE_TYPES)
                .anyMatch(supportedClass -> supportedClass == clazz);
    }

    @Override
    public Object deserialize(Class<?> resultingClass, JsonNode node) throws Exception {
        String value = node.asText();
        if (value == null || value.equals("null")) {
            return null;
        }
        Method parseMethod = resultingClass.getDeclaredMethod("parse", CharSequence.class);
        parseMethod.setAccessible(true);
        return parseMethod.invoke(null, value);
    }
}
