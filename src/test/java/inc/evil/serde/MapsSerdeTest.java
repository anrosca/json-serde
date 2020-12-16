package inc.evil.serde;

import inc.evil.serde.extension.JsonFile;
import inc.evil.serde.extension.JsonFileParameterSupplier;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static inc.evil.serde.util.TestUtils.assertJsonEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(JsonFileParameterSupplier.class)
public class MapsSerdeTest {
    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithHashMapFields(@JsonFile("/payloads/hashmap-fields.json") String expectedJson) {
        Map<String, User> users = new HashMap<>();
        users.put("Mike", new User("Mike"));
        users.put("John", new User("John"));
        MapFields mapFields = new MapFields(users, null);

        String actualJson = jsonMapper.serialize(mapFields);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithHashMapFields(@JsonFile("/payloads/hashmap-fields.json") String json) {
        MapFields actualInstance = jsonMapper.deserialize(json, MapFields.class);

        Map<String, User> users = new HashMap<>();
        users.put("Mike", new User("Mike"));
        users.put("John", new User("John"));
        MapFields expectedInstance = new MapFields(users, null);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithHashMapFieldsWithObjectKeys(@JsonFile("/payloads/hashmap-object-key.json") String expectedJson) {
        Map<User, String> users = new HashMap<>();
        users.put(new User("Mike"), "Mike");
        users.put(new User("John"), "John");
        MapFields mapFields = new MapFields(null, users);

        String actualJson = jsonMapper.serialize(mapFields);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithHashMapFieldsWithObjectKeys(@JsonFile("/payloads/hashmap-object-key.json") String json) {
        MapFields actualInstance = jsonMapper.deserialize(json, MapFields.class);

        Map<User, String> users = new HashMap<>();
        users.put(new User("Mike"), "Mike");
        users.put(new User("John"), "John");
        MapFields expectedInstance = new MapFields(null, users);
        assertEquals(expectedInstance, actualInstance);
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class MapFields {
        private final Map<String, User> users;
        private final Map<User, String> moreUsers;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class User {
        private final String name;
    }
}
