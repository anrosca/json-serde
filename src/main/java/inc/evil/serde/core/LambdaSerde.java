package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import inc.evil.serde.SerializerDeserializer;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public class LambdaSerde implements SerializerDeserializer {
    private final SerializerDeserializer delegate;

    public LambdaSerde(SerializerDeserializer delegate) {
        this.delegate = delegate;
    }

    @Override
    public JsonNode serialize(Object instance) {
        try {
            Method writeReplaceMethod = instance.getClass().getDeclaredMethod("writeReplace");
            writeReplaceMethod.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(instance);
            return delegate.serialize(serializedLambda, serializedLambda.getClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean canConsume(Class<?> clazz) {
        return clazz.getName().contains("/");
    }

    @Override
    public Object deserialize(JsonNode node) throws Exception {
        try {
            Method readResolveMethod = SerializedLambda.class.getDeclaredMethod("readResolve");
            readResolveMethod.setAccessible(true);
            return readResolveMethod.invoke(delegate.deserialize(SerializedLambda.class, node.get("value")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object deserialize(Class<?> resultingClass, JsonNode node) throws Exception {
        return null;
    }
}