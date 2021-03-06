package inc.evil.serde;

import inc.evil.serde.extension.JsonFile;
import inc.evil.serde.extension.JsonFileParameterSupplier;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static inc.evil.serde.cast.TestUtils.assertJsonEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(JsonFileParameterSupplier.class)
public class ArraysOfPrimitiveWrappersSerdeTest {

    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithArraysOfPrimitiveWrappersFields(@JsonFile("/payloads/primitive-wrapper-arrays.json") String expectedJson) {
        ArraysOfPrimitiveWrappers arraysOfPrimitiveWrappers = new ArraysOfPrimitiveWrappers(
                new Boolean[]{true, false},
                new Byte[]{1, 2},
                new Character[]{'W', 'Q'},
                new Short[]{3, 4},
                new Integer[] { 5, 6},
                new Long[] {7L, 8L},
                new Float[] {0.5f, 1.5f},
                new Double[] {1.55, 3.14}
        );

        String actualJson = jsonMapper.serialize(arraysOfPrimitiveWrappers);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithArraysOfPrimitiveWrappersFields(@JsonFile("/payloads/primitive-wrapper-arrays.json") String json) {
        ArraysOfPrimitiveWrappers actualInstance = jsonMapper.deserialize(json, ArraysOfPrimitiveWrappers.class);

        ArraysOfPrimitiveWrappers expectedInstance = new ArraysOfPrimitiveWrappers(
                new Boolean[]{true, false},
                new Byte[]{1, 2},
                new Character[]{'W', 'Q'},
                new Short[]{3, 4},
                new Integer[] { 5, 6},
                new Long[] {7L, 8L},
                new Float[] {0.5f, 1.5f},
                new Double[] {1.55, 3.14}
        );
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectsWithNullArraysOfPrimitives(@JsonFile("/payloads/null-primitive-wrapper-arrays.json") String expectedJson) {
        ArraysOfPrimitiveWrappers arraysOfPrimitiveWrappers = new ArraysOfPrimitiveWrappers(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        String actualJson = jsonMapper.serialize(arraysOfPrimitiveWrappers);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectsWithNullArraysOfPrimitives(@JsonFile("/payloads/null-primitive-wrapper-arrays.json") String json) {
        ArraysOfPrimitiveWrappers actualInstance = jsonMapper.deserialize(json, ArraysOfPrimitiveWrappers.class);

        ArraysOfPrimitiveWrappers expectedInstance = new ArraysOfPrimitiveWrappers(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        assertEquals(expectedInstance, actualInstance);
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class ArraysOfPrimitiveWrappers {
        private final Boolean[] booleanArray;
        private final Byte[] byteArray;
        private final Character[] charArray;
        private final Short[] shortArray;
        private final Integer[] integerArray;
        private final Long[] longArray;
        private final Float[] floatArray;
        private final Double[] doubleArray;
    }
}
