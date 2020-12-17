package inc.evil.serde;

import java.util.Set;

public class JsonMapper {
    private final SerdeFactory factory = new SerdeFactory();

    public String serialize(Object instance) {
        JsonSerde jsonSerde = factory.defaultSerde();
        return jsonSerde.serialize(instance);
    }

    public String serialize(Object instance, Set<SerdeFeature> options) {
        JsonSerde jsonSerde = factory.from(options);
        return jsonSerde.serialize(instance);
    }

    public <T> T deserialize(String json, Class<T> targetClass) {
        JsonSerde jsonSerde = factory.defaultSerde();
        return jsonSerde.deserialize(json, targetClass);
    }
}
