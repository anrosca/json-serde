package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerializerDeserializer;

import java.util.List;

public class ObjectSerializer {
    private final ObjectSerde objectSerde;

    public ObjectSerializer(SerdeContext serdeContext) {
        List<SerializerDeserializer> delegates = List.of(
                new ArraySerde(serdeContext, List.of(new CommonMapSerde(serdeContext), new CommonCollectionSerde(serdeContext))),
                new PrimitiveTypeSerde(serdeContext)

        );
        this.objectSerde = new ObjectSerde(serdeContext, delegates);
    }

    public JsonNode serialize(Object instance, Class<?> type) {
        return objectSerde.serialize(instance, type);
    }
}
