package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnumSerdeTest {
    private final EnumSerde enumSerde = new EnumSerde();

    enum Seasons {SPRING, SUMMER, AUTUMN, WINTER}

    @Test
    public void shouldConsumeOnlyEnumTypes() {
        assertTrue(enumSerde.canConsume(Seasons.class));
    }

    @Test
    public void shouldNotConsumeTextualJsonNodes() {
        assertFalse(enumSerde.canConsume(new TextNode("SPRING")));
    }

    @Test
    public void shouldBeAbleToSerializeEnums() {
        ObjectNode expectedNode = new ObjectNode(JsonNodeFactory.instance);
        expectedNode.set("type", new TextNode(Seasons.class.getName()));
        expectedNode.set("value", new TextNode(Seasons.WINTER.toString()));

        JsonNode serializedNode = enumSerde.serialize(Seasons.WINTER);

        assertEquals(expectedNode, serializedNode);
    }

    @Test
    public void shouldBeAbleToDeserializeEnums() throws Exception {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.set("type", new TextNode(Seasons.class.getName()));
        node.set("value", new TextNode(Seasons.WINTER.toString()));

        Seasons deserializedInstance = (Seasons) enumSerde.deserialize(Seasons.class, node);

        assertEquals(Seasons.WINTER, deserializedInstance);
    }

    @Test
    public void shouldThrowIllegalArgumentException_whenSerializingANonEnumInstance() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> enumSerde.serialize(new Object()));
        assertEquals("java.lang.Object can't be serialized by inc.evil.serde.core.EnumSerde", exception.getMessage());
    }

    @Test
    public void shouldBeAbleToDeserializeNullEnumConstants() throws Exception {
        assertNull(enumSerde.deserialize(Seasons.class, new TextNode("null")));
        assertNull(enumSerde.deserialize(Seasons.class, NullNode.getInstance()));
    }

    @Test
    public void shouldThrowIllegalArgumentException_whenDeserializingAnInvalidEnumConstant() {
        Exception exception = assertThrows(EnumSerde.EnumDeserializationException.class,
                () -> enumSerde.deserialize(Seasons.class, new TextNode("fall")));
        assertEquals("No such enum constant inc.evil.serde.core.EnumSerdeTest$Seasons.fall", exception.getMessage());
    }
}
