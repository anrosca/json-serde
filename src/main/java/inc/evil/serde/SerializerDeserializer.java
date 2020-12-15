package inc.evil.serde;

import com.fasterxml.jackson.databind.JsonNode;

public interface SerializerDeserializer {

    JsonNode serialize(Object instance);

    default JsonNode serialize(Object instance, Class<?> type) {
        return serialize(instance);
    }

    boolean canConsume(Class<?> clazz);

    default boolean canConsume(JsonNode node) {
        return false;
    }

    Object deserialize(Class<?> resultingClass, JsonNode node) throws Exception;

    default Object deserialize(JsonNode node) throws Exception {
        return null;
    }
}
