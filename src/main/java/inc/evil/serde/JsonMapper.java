package inc.evil.serde;

public class JsonMapper {
    public String serialize(Object instance) {
        JsonSerde jsonSerde = new JsonSerde();
        return jsonSerde.serialize(instance);
    }

    public <T> T deserialize(String json, Class<T> targetClass) {
        JsonSerde jsonSerde = new JsonSerde();
        return jsonSerde.deserialize(json, targetClass);
    }
}
