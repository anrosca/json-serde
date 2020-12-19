package inc.evil.serde;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class UnwrappedArraysTest {
    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void shouldBeAbleToSerializeAndDeserialize_unwrappedArrays() {
        int[] numbers = new int[]{1, 2, 3};

        String json = jsonMapper.serialize(numbers);

        int[] actualNumbers = jsonMapper.deserialize(json, int[].class);

        assertArrayEquals(numbers, actualNumbers);
    }
}
