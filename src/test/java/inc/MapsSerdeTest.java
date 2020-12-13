package inc;

import inc.evil.serde.JsonMapper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static inc.evil.serde.util.TestUtils.assertJsonEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapsSerdeTest {
    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithHashMapFields() {
        Map<String, User> users = new HashMap<>();
        users.put("Mike", new User("Mike"));
        users.put("John", new User("John"));
        MapFields mapFields = new MapFields(users);

        String actualJson = jsonMapper.serialize(mapFields);

        String expectedJson = """
                {
                  "targetClass": "inc.MapsSerdeTest$MapFields",
                  "state": {
                    "users": {
                      "type": "java.util.HashMap",
                      "value": [
                        {
                          "key": "Mike",
                          "value": {
                            "targetClass": "inc.MapsSerdeTest$User",
                            "state": {"name": {"type": "java.lang.String", "value": "Mike"}},
                            "__id": 2
                          }
                        },
                        {
                          "key": "John",
                          "value": {
                            "targetClass": "inc.MapsSerdeTest$User",
                            "state": {"name": {"type": "java.lang.String", "value": "John"}},
                            "__id": 3
                          }
                        }
                      ]
                    }
                  },
                  "__id": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithHashMapFields() {
        String json = """
                {
                  "targetClass": "inc.MapsSerdeTest$MapFields",
                  "state": {
                    "users": {
                      "type": "java.util.HashMap",
                      "value": [
                        {
                          "key": "Mike",
                          "value": {
                            "targetClass": "inc.MapsSerdeTest$User",
                            "state": {"name": {"type": "java.lang.String", "value": "Mike"}},
                            "__id": 2
                          }
                        },
                        {
                          "key": "John",
                          "value": {
                            "targetClass": "inc.MapsSerdeTest$User",
                            "state": {"name": {"type": "java.lang.String", "value": "John"}},
                            "__id": 3
                          }
                        }
                      ]
                    }
                  },
                  "__id": 1
                }""";

        MapFields actualInstance = jsonMapper.deserialize(json, MapFields.class);

        Map<String, User> users = new HashMap<>();
        users.put("Mike", new User("Mike"));
        users.put("John", new User("John"));
        MapFields expectedInstance = new MapFields(users);
        assertEquals(expectedInstance, actualInstance);
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class MapFields {
        private final Map<String, User> users;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class User {
        private final String name;
    }
}
