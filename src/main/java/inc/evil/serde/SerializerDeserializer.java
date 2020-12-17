package inc.evil.serde;

import com.fasterxml.jackson.databind.JsonNode;

public interface SerializerDeserializer {

    JsonNode serialize(Object instance, SerdeContext serdeContext);

    default JsonNode serialize(Object instance, Class<?> type, SerdeContext serdeContext) {
        return serialize(instance, serdeContext);
    }

    boolean canConsume(Class<?> clazz);

    default boolean canConsume(JsonNode node) {
        return false;
    }

    Object deserialize(Class<?> resultingClass, JsonNode node, SerdeContext serdeContext) throws Exception;

    default Object deserialize(JsonNode node, SerdeContext serdeContext) throws Exception {
        return null;
    }
}
