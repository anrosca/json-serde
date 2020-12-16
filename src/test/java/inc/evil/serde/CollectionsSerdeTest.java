package inc.evil.serde;

import inc.evil.serde.extension.JsonFile;
import inc.evil.serde.extension.JsonFileParameterSupplier;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static inc.evil.serde.util.TestUtils.assertJsonEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(JsonFileParameterSupplier.class)
public class CollectionsSerdeTest {

    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void shouldBeAbleToSerializeToJson_ArrayListFields(@JsonFile("/payloads/array-list-fields.json") String expectedJson) {
        List<User> users = new ArrayList<>();
        users.add(new User("Mike"));
        ArrayListFields arrayListFields = new ArrayListFields(users);

        String actualJson = jsonMapper.serialize(arrayListFields);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_ArrayListFields(@JsonFile("/payloads/array-list-fields.json") String json) {
        ArrayListFields actualInstance = jsonMapper.deserialize(json, ArrayListFields.class);

        List<User> users = new ArrayList<>();
        users.add(new User("Mike"));
        ArrayListFields expectedInstance = new ArrayListFields(users);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_emptyArrayListFields(@JsonFile("/payloads/empty-array-list.json") String expectedJson) {
        ArrayListFields arrayListFields = new ArrayListFields(new ArrayList<>());

        String actualJson = jsonMapper.serialize(arrayListFields);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_emptyArrayListFields(@JsonFile("/payloads/empty-array-list.json") String json) {
        ArrayListFields actualInstance = jsonMapper.deserialize(json, ArrayListFields.class);

        ArrayListFields expectedInstance = new ArrayListFields(new ArrayList<>());
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_HashSetFields(@JsonFile("/payloads/hash-set-fields.json") String expectedJson) {
        Set<User> users = new HashSet<>();
        users.add(new User("Mike"));
        HashSetFields hashSetFields = new HashSetFields(users);

        String actualJson = jsonMapper.serialize(hashSetFields);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_HashSetFields(@JsonFile("/payloads/hash-set-fields.json") String json) {
        HashSetFields actualInstance = jsonMapper.deserialize(json, HashSetFields.class);

        Set<User> users = new HashSet<>();
        users.add(new User("Mike"));
        HashSetFields expectedInstance = new HashSetFields(users);
        assertEquals(expectedInstance, actualInstance);
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class ArrayListFields {
        private final List<User> strings;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class HashSetFields {
        private final Set<User> strings;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class User {
        private final String name;
    }
}
