package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerializerDeserializer;

public class PrimitiveTypeSerde implements SerializerDeserializer {

    @Override
    public JsonNode serialize(Object instance, SerdeContext serdeContext) {
        return serdeContext.serializeValue(instance);
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return clazz.isPrimitive();
    }

    @Override
    public Object deserialize(Class<?> resultingClass, JsonNode node, SerdeContext serdeContext) throws Exception {
        return serdeContext.deserialize(node.toString(), resultingClass);
    }
}
