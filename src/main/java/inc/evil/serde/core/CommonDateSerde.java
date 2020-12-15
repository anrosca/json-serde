package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerializerDeserializer;

import java.lang.reflect.Method;
import java.time.*;

public class CommonDateSerde implements SerializerDeserializer {
    private final SerdeContext serdeContext;

    public CommonDateSerde(SerdeContext serdeContext) {
        this.serdeContext = serdeContext;
    }

    @Override
    public JsonNode serialize(Object instance) {
        return new TextNode(instance.toString());
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return clazz == LocalDateTime.class || clazz == LocalDate.class ||
               clazz == OffsetDateTime.class || clazz == ZonedDateTime.class ||
               clazz == Period.class || clazz == Duration.class;
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
