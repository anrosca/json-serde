package inc.evil.serde;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import static inc.evil.serde.util.TestUtils.assertJsonEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArraysOfPrimitiveWrappersSerdeTest {

    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithArraysOfPrimitiveWrappersFields() {
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

        String expectedJson = """
            {
              "targetClass": "inc.evil.serde.ArraysOfPrimitiveWrappersSerdeTest$ArraysOfPrimitiveWrappers",
              "state": {
                "booleanArray": {"type": "[Ljava.lang.Boolean;", "value": [true, false]},
                "byteArray": {"type": "[Ljava.lang.Byte;", "value": [1, 2]},
                "charArray": {"type": "[Ljava.lang.Character;", "value": [87, 81]},
                "shortArray": {"type": "[Ljava.lang.Short;", "value": [3, 4]},
                "integerArray": {"type": "[Ljava.lang.Integer;", "value": [5, 6]},
                "longArray": {"type": "[Ljava.lang.Long;", "value": [7, 8]},
                "floatArray": {"type": "[Ljava.lang.Float;", "value": [0.5, 1.5]},
                "doubleArray": {"type": "[Ljava.lang.Double;", "value": [1.55, 3.14]}
              },
              "__id": 1
            }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithArraysOfPrimitiveWrappersFields() {
        String json = """
            {
              "targetClass": "inc.evil.serde.ArraysOfPrimitiveWrappersSerdeTest$ArraysOfPrimitiveWrappers",
              "state": {
                "booleanArray": {"type": "[Ljava.lang.Boolean;", "value": [true, false]},
                "byteArray": {"type": "[Ljava.lang.Byte;", "value": [1, 2]},
                "charArray": {"type": "[Ljava.lang.Character;", "value": [87, 81]},
                "shortArray": {"type": "[Ljava.lang.Short;", "value": [3, 4]},
                "integerArray": {"type": "[Ljava.lang.Integer;", "value": [5, 6]},
                "longArray": {"type": "[Ljava.lang.Long;", "value": [7, 8]},
                "floatArray": {"type": "[Ljava.lang.Float;", "value": [0.5, 1.5]},
                "doubleArray": {"type": "[Ljava.lang.Double;", "value": [1.55, 3.14]}
              },
              "__id": 1
            }""";

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
    public void shouldBeAbleToSerializeToJson_objectsWithNullArraysOfPrimitives() {
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

        String expectedJson = """
            {
              "targetClass": "inc.evil.serde.ArraysOfPrimitiveWrappersSerdeTest$ArraysOfPrimitiveWrappers",
              "state": {
                "booleanArray": null,
                "byteArray": null,
                "charArray": null,
                "shortArray": null,
                "integerArray": null,
                "longArray": null,
                "floatArray": null,
                "doubleArray": null
              },
              "__id": 1
            }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectsWithNullArraysOfPrimitives() {
        String json = """
            {
              "targetClass": "inc.evil.serde.ArraysOfPrimitiveWrappersSerdeTest$ArraysOfPrimitiveWrappers",
              "state": {
                "booleanArray": null,
                "byteArray": null,
                "charArray": null,
                "shortArray": null,
                "integerArray": null,
                "longArray": null,
                "floatArray": null,
                "doubleArray": null
              },
              "__id": 1
            }""";

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
