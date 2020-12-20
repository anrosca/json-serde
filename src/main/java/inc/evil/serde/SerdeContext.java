package inc.evil.serde;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface SerdeContext {

    JsonNode serializeValue(Object instance);

    Object deserializeValue(JsonNode fieldNode) throws Exception;

    <T> T deserialize(String json, Class<T> clazz);

    void addDeserializedInstance(String objectId, Object instance);

    long generateObjectId();

    boolean wasSerialized(Object instance);

    void addSerializedInstance(Object instance, ObjectNode rootNode);

    JsonNode getPreviouslySerializedInstance(Object instance);

    Object getPreviouslyDeserializedInstance(String value);
}
