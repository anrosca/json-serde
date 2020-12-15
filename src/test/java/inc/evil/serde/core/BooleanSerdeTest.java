package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanSerdeTest {
    private final BooleanSerde booleanSerde = new BooleanSerde();

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
        JsonNode serializedNode = booleanSerde.serialize(true);

        assertEquals(BooleanNode.valueOf(true), serializedNode);
    }

    @Test
    public void shouldBeAbleToSerializeAtomicBooleans() {
        JsonNode serializedNode = booleanSerde.serialize(new AtomicBoolean(false));

        assertEquals(BooleanNode.valueOf(false), serializedNode);
    }

    @Test
    public void shouldBeAbleToDeserializeAtomicBooleans() throws Exception {
        BooleanNode node = BooleanNode.valueOf(true);

        AtomicBoolean deserializedInstance = (AtomicBoolean) booleanSerde.deserialize(AtomicBoolean.class, node);

        assertTrue(deserializedInstance.get());
    }

    @Test
    public void shouldBeAbleToDeserializeBooleans() throws Exception {
        BooleanNode node = BooleanNode.valueOf(false);

        Boolean deserializedInstance = (Boolean) booleanSerde.deserialize(Boolean.class, node);

        assertFalse(deserializedInstance);
    }

    @Test
    public void shouldThrowIllegalArgumentException_whenSerializingANonBooleanInstance() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> booleanSerde.serialize(new Object()));
        assertEquals("java.lang.Object can't be serialized by inc.evil.serde.core.BooleanSerde", exception.getMessage());
    }
}
