package inc.evil.serde.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void assertJsonEquals(String expectedJson, String actualJson) {
        try {
            assertEquals(objectMapper.readTree(expectedJson), objectMapper.readTree(actualJson));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
