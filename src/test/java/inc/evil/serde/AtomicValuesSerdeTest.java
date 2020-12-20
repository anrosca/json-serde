package inc.evil.serde;

import inc.evil.serde.extension.JsonFile;
import inc.evil.serde.extension.JsonFileParameterSupplier;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static inc.evil.serde.cast.TestUtils.assertJsonEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(JsonFileParameterSupplier.class)
public class AtomicValuesSerdeTest {
    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithAtomicValuesFields(@JsonFile("/payloads/atomic-values.json") String expectedJson) {
        AtomicNumbers atomicNumbers = new AtomicNumbers(
                new AtomicInteger(42),
                new AtomicLong(66),
                new AtomicBoolean(true)
        );

        String actualJson = jsonMapper.serialize(atomicNumbers);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithAtomicValuesFields(@JsonFile("/payloads/atomic-values.json")String json) {
        AtomicNumbers actualInstance = jsonMapper.deserialize(json, AtomicNumbers.class);

        AtomicNumbers expectedInstance = new AtomicNumbers(
                new AtomicInteger(42),
                new AtomicLong(66),
                new AtomicBoolean(true)
        );
        assertEquals(expectedInstance.atomicInteger.get(), actualInstance.atomicInteger.get());
        assertEquals(expectedInstance.atomicLong.get(), actualInstance.atomicLong.get());
        assertEquals(expectedInstance.atomicBoolean.get(), actualInstance.atomicBoolean.get());
    }

    @Test
    public void shouldBeAbleToSerializeToJson_NullAtomicValues(@JsonFile("/payloads/null-atomic-values.json") String expectedJson) {
        AtomicNumbers atomicNumbers = new AtomicNumbers(null, null, null);

        String actualJson = jsonMapper.serialize(atomicNumbers);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_NullAtomicValues(@JsonFile("/payloads/null-atomic-values.json") String json) {
        AtomicNumbers actualInstance = jsonMapper.deserialize(json, AtomicNumbers.class);

        AtomicNumbers expectedInstance = new AtomicNumbers(null, null, null);
        assertEquals(expectedInstance, actualInstance);
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class AtomicNumbers {
        private final AtomicInteger atomicInteger;
        private final AtomicLong atomicLong;
        private final AtomicBoolean atomicBoolean;
    }
}
