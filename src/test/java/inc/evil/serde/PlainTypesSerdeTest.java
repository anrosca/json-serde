package inc.evil.serde;

import lombok.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlainTypesSerdeTest {
    private final JsonMapper jsonMapper = new JsonMapper();

    @MethodSource("providePlainArrays")
    @ParameterizedTest
    public void shouldBeAbleToSerializeAndDeserialize_plainArrays(Object initialArray) {
        String json = jsonMapper.serialize(initialArray);

        Object actualArray = jsonMapper.deserialize(json, initialArray.getClass());

        assertArrayEquals(initialArray, actualArray);
    }

    static Stream<Arguments> providePlainArrays() {
        return Stream.of(
                Arguments.of((Object) new byte[]{1, 2}),
                Arguments.of((Object) new boolean[]{true, false}),
                Arguments.of((Object) new char[]{'1', '2'}),
                Arguments.of((Object) new short[]{3, 4}),
                Arguments.of((Object) new int[]{1, 2}),
                Arguments.of((Object) new long[]{1L, 2L}),
                Arguments.of((Object) new float[]{1f, 2f}),
                Arguments.of((Object) new double[]{1d, 2d}),
                Arguments.of((Object) new Boolean[]{true, false}),
                Arguments.of((Object) new Byte[]{1, 2}),
                Arguments.of((Object) new Character[]{'1', '2'}),
                Arguments.of((Object) new Short[]{1, 2}),
                Arguments.of((Object) new Integer[]{1, 2}),
                Arguments.of((Object) new Long[]{1L, 2L}),
                Arguments.of((Object) new Float[]{1f, 2f}),
                Arguments.of((Object) new Double[]{1d, 2d}),
                Arguments.of((Object) new String[]{"Mike", "Dennis"}),
                Arguments.of((Object) new Object[]{"Mike", 42, 'W', new User("John"), new ArrayList<>(singletonList("uno"))}),
                Arguments.of((Object) new User[]{new User("Mike")})
        );
    }

    @MethodSource("providePlainTypes")
    @ParameterizedTest
    public void shouldBeAbleToSerializeAndDeserialize_plainTypes(Object valueToSerialize) {
        String json = jsonMapper.serialize(valueToSerialize);

        Object actualValue = jsonMapper.deserialize(json, valueToSerialize.getClass());

        assertEquals(valueToSerialize, actualValue);
    }

    static Stream<Arguments> providePlainTypes() {
        return Stream.of(
                Arguments.of(true),
                Arguments.of((byte) 1),
                Arguments.of('w'),
                Arguments.of((short) 2),
                Arguments.of(3),
                Arguments.of(4L),
                Arguments.of(5.0f),
                Arguments.of(6D),
                Arguments.of("__funky__"),
                Arguments.of(new HashMap<>(Collections.singletonMap("one", 1)))
        );
    }

    private static void assertArrayEquals(Object expected, Object actual) {
        if (expected != actual) {
            assertEquals(Array.getLength(expected), Array.getLength(actual));
            for (int i = 0; i < Array.getLength(expected); ++i) {
                if (!Array.get(expected, i).equals(Array.get(actual, i))) {
                    Assertions.fail("Arrays are not equal. Expected: " +
                            arrayToString(expected) + " of class " + expected.getClass().getName() +
                            ". Actual: " + arrayToString(actual) + " of class " + actual.getClass().getName());
                }
            }
        }
    }

    private static String arrayToString(Object array) {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (int i = 0; i < Array.getLength(array); ++i) {
            joiner.add(Array.get(array, i).toString());
        }
        return joiner.toString();
    }

    @Value
    public static class User {
        String firstName;
    }
}
