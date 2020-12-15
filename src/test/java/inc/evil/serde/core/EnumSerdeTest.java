package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
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
        JsonNode serializedNode = enumSerde.serialize(Seasons.WINTER);

        assertEquals(Seasons.WINTER.toString(), serializedNode.asText());
    }

    @Test
    public void shouldBeAbleToDeserializeEnums() throws Exception {
        TextNode node = new TextNode("WINTER");

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
