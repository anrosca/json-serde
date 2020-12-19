package inc.evil.serde;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlainTypesSerdeTest {
    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void shouldBeAbleToSerializeAndDeserialize_plainArrays() {
        int[] numbers = new int[]{1, 2, 3};

        String json = jsonMapper.serialize(numbers);

        int[] actualNumbers = jsonMapper.deserialize(json, int[].class);

        assertArrayEquals(numbers, actualNumbers);
    }

    @Test
    public void shouldBeAbleToSerializeAndDeserialize_plainIntegerArrays() {
        Integer[] numbers = new Integer[]{1, 2, 3};

        String json = jsonMapper.serialize(numbers);

        Integer[] actualNumbers = jsonMapper.deserialize(json, Integer[].class);

        assertArrayEquals(numbers, actualNumbers);
    }

    @Test
    public void shouldBeAbleToSerializeAndDeserialize_plainHashMap() {
        Map<String, Integer> expectedMap = new HashMap<>(Collections.singletonMap("one", 1));

        String json = jsonMapper.serialize(expectedMap);

        Map<String, Integer> actualMap = jsonMapper.deserialize(json, Map.class);

        assertEquals(expectedMap, actualMap);
    }

    @Test
    public void shouldBeAbleToSerializeAndDeserialize_plainString() {
        String expectedString = "Welcome!!!";

        String json = jsonMapper.serialize(expectedString);

        String actualString = jsonMapper.deserialize(json, String.class);

        assertEquals(expectedString, actualString);
    }

    @Test
    public void shouldBeAbleToSerializeAndDeserialize_plainInt() {
        int expectedInt = 42;

        String json = jsonMapper.serialize(expectedInt);

        int actualInt = jsonMapper.deserialize(json, int.class);

        assertEquals(expectedInt, actualInt);
    }
}
