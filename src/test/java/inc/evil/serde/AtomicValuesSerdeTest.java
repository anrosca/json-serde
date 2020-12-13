package inc.evil.serde;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static inc.evil.serde.util.TestUtils.assertJsonEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AtomicValuesSerdeTest {
    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithAtomicValuesFields() {
        AtomicNumbers atomicNumbers = new AtomicNumbers(
                new AtomicInteger(42),
                new AtomicLong(66),
                new AtomicBoolean(true)
        );

        String actualJson = jsonMapper.serialize(atomicNumbers);

        String expectedJson = """
                {
                  "targetClass" : "inc.evil.serde.AtomicValuesSerdeTest$AtomicNumbers",
                  "state" : {
                    "atomicInteger" : {"type" : "java.util.concurrent.atomic.AtomicInteger", "value" : 42},
                    "atomicLong" : {"type" : "java.util.concurrent.atomic.AtomicLong", "value" : 66},
                    "atomicBoolean": {"type": "java.util.concurrent.atomic.AtomicBoolean", "value": true}
                  },
                  "__idRef" : 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithAtomicValuesFields() {
        String json = """
                {
                  "targetClass" : "inc.evil.serde.AtomicValuesSerdeTest$AtomicNumbers",
                  "state" : {
                    "atomicInteger" : {"type" : "java.util.concurrent.atomic.AtomicInteger", "value" : 42},
                    "atomicLong" : {"type" : "java.util.concurrent.atomic.AtomicLong", "value" : 66},
                    "atomicBoolean": {"type": "java.util.concurrent.atomic.AtomicBoolean", "value": true}
                  },
                  "__idRef" : 1
                }""";

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
    public void shouldBeAbleToSerializeToJson_NullAtomicValues() {
        AtomicNumbers atomicNumbers = new AtomicNumbers(null, null, null);

        String actualJson = jsonMapper.serialize(atomicNumbers);

        String expectedJson = """
            {
              "targetClass": "inc.evil.serde.AtomicValuesSerdeTest$AtomicNumbers",
              "state": {
                "atomicInteger": {"type": "java.util.concurrent.atomic.AtomicInteger", "value": null},
                "atomicLong": {"type": "java.util.concurrent.atomic.AtomicLong", "value": null},
                "atomicBoolean": {"type": "java.util.concurrent.atomic.AtomicBoolean", "value": null}
              },
              "__idRef": 1
            }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_NullAtomicValues() {
        String json = """
            {
              "targetClass": "inc.evil.serde.AtomicValuesSerdeTest$AtomicNumbers",
              "state": {
                "atomicInteger": {"type": "java.util.concurrent.atomic.AtomicInteger", "value": null},
                "atomicLong": {"type": "java.util.concurrent.atomic.AtomicLong", "value": null},
                "atomicBoolean": {"type": "java.util.concurrent.atomic.AtomicBoolean", "value": null}
              },
              "__idRef": 1
            }""";

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
