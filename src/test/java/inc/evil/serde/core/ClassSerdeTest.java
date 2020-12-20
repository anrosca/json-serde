package inc.evil.serde.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import inc.evil.serde.SerdeContext;
import inc.evil.serde.SerdeFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClassSerdeTest {
    private final ClassSerde classSerde = new ClassSerde();
    private final SerdeContext serdeContext = new SerdeFactory().defaultSerde();

    @Test
    public void shouldConsumeOnlyClassTypes() {
        assertTrue(classSerde.canConsume(Class.class));
    }

    @Test
    public void shouldNotConsumeTextualJsonNodes() {
        assertFalse(classSerde.canConsume(new TextNode("java.lang.String")));
    }

    @Test
    public void shouldBeAbleToSerializeClasses() {
        JsonNode serializedNode = classSerde.serialize(String.class, serdeContext);

        assertEquals(String.class.getName(), serializedNode.get("value").asText());
        assertEquals(Class.class.getName(), serializedNode.get("type").asText());
    }

    @Test
    public void shouldBeAbleToDeserializeClasses() throws Exception {
        TextNode node = new TextNode("java.lang.String");

        Class<?> deserializedInstance = (Class<?>) classSerde.deserialize(Class.class, node, serdeContext);

        assertEquals(String.class, deserializedInstance);
    }

    @Test
    public void shouldThrowIllegalArgumentException_whenSerializingANonClassInstance() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> classSerde.serialize(new Object(), serdeContext));
        assertEquals("java.lang.Object can't be serialized by inc.evil.serde.core.ClassSerde", exception.getMessage());
    }
}
