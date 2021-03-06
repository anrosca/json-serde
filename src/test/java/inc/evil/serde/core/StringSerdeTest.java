package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerdeFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringSerdeTest {
    private final StringSerde stringSerde = new StringSerde();
    private final SerdeContext serdeContext = new SerdeFactory().defaultSerde();

    @Test
    public void shouldConsumeOnlyStringTypes() {
        assertTrue(stringSerde.canConsume(String.class));
        assertTrue(stringSerde.canConsume(StringBuilder.class));
        assertTrue(stringSerde.canConsume(StringBuffer.class));
    }

    @Test
    public void shouldConsumeTextualJsonNodes() {
        assertTrue(stringSerde.canConsume(new TextNode("Hello")));
    }

    @Test
    public void shouldRejectNonTextualJsonNodes() {
        assertFalse(stringSerde.canConsume(new IntNode(42)));
    }

    @Test
    public void shouldBeAbleToSerializeStrings() {
        JsonNode serializedNode = stringSerde.serialize("yay", serdeContext);

        assertEquals("yay", serializedNode.asText());
    }

    @Test
    public void shouldBeAbleToSerializeStringBuilders() {
        JsonNode serializedNode = stringSerde.serialize(new StringBuilder("builder"), serdeContext);

        assertEquals("builder", serializedNode.asText());
    }

    @Test
    public void shouldBeAbleToDeserializeStrings() throws Exception {
        TextNode node = new TextNode("Hello");

        String deserializedInstance = (String) stringSerde.deserialize(String.class, node, serdeContext);

        assertEquals("Hello", deserializedInstance);
    }

    @Test
    public void shouldBeAbleToDeserializeStringBuilders() throws Exception {
        TextNode node = new TextNode("Hello");

        StringBuilder deserializedInstance = (StringBuilder) stringSerde.deserialize(StringBuilder.class, node, serdeContext);

        assertEquals("Hello", deserializedInstance.toString());
    }

    @Test
    public void shouldBeAbleToDeserializeStringBuffers() throws Exception {
        TextNode node = new TextNode("Hello");

        StringBuffer deserializedInstance = (StringBuffer) stringSerde.deserialize(StringBuffer.class, node, serdeContext);

        assertEquals("Hello", deserializedInstance.toString());
    }

    @Test
    public void shouldThrowIllegalArgumentException_whenSerializingANonBooleanInstance() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> stringSerde.serialize(new Object(), serdeContext));
        assertEquals("java.lang.Object can't be serialized by inc.evil.serde.core.StringSerde", exception.getMessage());
    }
}
