package inc.evil.serde;

import com.fasterxml.jackson.databind.JsonNode;

public interface SerdeContext {

    JsonNode serializeValue(Object instance);

    Object getNodeValue(JsonNode fieldNode) throws Exception;

    <T> T deserialize(String json, Class<T> clazz);
}
