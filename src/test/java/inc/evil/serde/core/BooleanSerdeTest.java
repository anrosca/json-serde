package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerdeFactory;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanSerdeTest {
    private final BooleanSerde booleanSerde = new BooleanSerde();
    private final SerdeContext serdeContext = new SerdeFactory().defaultSerde();

    @Test
    public void shouldConsumeOnlyBooleanAndAtomicBoolean() {
        assertTrue(booleanSerde.canConsume(Boolean.class));
        assertTrue(booleanSerde.canConsume(AtomicBoolean.class));
    }

    @Test
    public void shouldConsumeBooleanJsonNodes() {
        assertTrue(booleanSerde.canConsume(BooleanNode.valueOf(true)));
    }

    @Test
    public void shouldRejectNonBooleanJsonNodes() {
        assertFalse(booleanSerde.canConsume(new TextNode("Hello")));
    }

    @Test
    public void shouldBeAbleToSerializePrimitiveBooleans() {
        JsonNode serializedNode = booleanSerde.serialize(true, serdeContext);

        assertEquals(BooleanNode.valueOf(true), serializedNode);
    }

    @Test
    public void shouldBeAbleToSerializeAtomicBooleans() {
        JsonNode serializedNode = booleanSerde.serialize(new AtomicBoolean(false), serdeContext);

        ObjectNode expectedNode = new ObjectNode(JsonNodeFactory.instance);
        expectedNode.set("type", new TextNode(AtomicBoolean.class.getName()));
        expectedNode.set("value", BooleanNode.valueOf(false));
        assertEquals(expectedNode, serializedNode);
    }

    @Test
    public void shouldBeAbleToDeserializeAtomicBooleans() throws Exception {
        BooleanNode node = BooleanNode.valueOf(true);

        AtomicBoolean deserializedInstance = (AtomicBoolean) booleanSerde.deserialize(AtomicBoolean.class, node, serdeContext);

        assertTrue(deserializedInstance.get());
    }

    @Test
    public void shouldBeAbleToDeserializeBooleans() throws Exception {
        BooleanNode node = BooleanNode.valueOf(false);

        Boolean deserializedInstance = (Boolean) booleanSerde.deserialize(Boolean.class, node, serdeContext);

        assertFalse(deserializedInstance);
    }

    @Test
    public void shouldThrowIllegalArgumentException_whenSerializingANonBooleanInstance() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> booleanSerde.serialize(new Object(), serdeContext));
        assertEquals("java.lang.Object can't be serialized by inc.evil.serde.core.BooleanSerde", exception.getMessage());
    }
}
