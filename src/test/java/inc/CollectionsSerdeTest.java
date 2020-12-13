package inc;

import inc.evil.serde.JsonMapper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static inc.evil.serde.util.TestUtils.assertJsonEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionsSerdeTest {

    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void shouldBeAbleToSerializeToJson_ArrayListFields() {
        List<User> users = new ArrayList<>();
        users.add(new User("Mike"));
        ArrayListFields arrayListFields = new ArrayListFields(users);

        String actualJson = jsonMapper.serialize(arrayListFields);

        String expectedJson = """
                {
                  "targetClass": "inc.CollectionsSerdeTest$ArrayListFields",
                  "state": {
                    "strings": {
                      "type": "java.util.ArrayList",
                      "value": [
                        {
                          "targetClass": "inc.CollectionsSerdeTest$User",
                          "state": {
                            "name": {"type": "java.lang.String", "value": "Mike"}
                          },
                          "__idRef": 2
                        }
                      ]
                    }
                  },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_ArrayListFields() {
        String json = """
                {
                  "targetClass": "inc.CollectionsSerdeTest$ArrayListFields",
                  "state": {
                    "strings": {
                      "type": "java.util.ArrayList",
                      "value": [
                        {
                          "targetClass": "inc.CollectionsSerdeTest$User",
                          "state": {
                            "name": {"type": "java.lang.String", "value": "Mike"}
                          },
                          "__idRef": 2
                        }
                      ]
                    }
                  },
                  "__idRef": 1
                }""";

        ArrayListFields actualInstance = jsonMapper.deserialize(json, ArrayListFields.class);

        List<User> users = new ArrayList<>();
        users.add(new User("Mike"));
        ArrayListFields expectedInstance = new ArrayListFields(users);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_emptyArrayListFields() {
        ArrayListFields arrayListFields = new ArrayListFields(new ArrayList<>());

        String actualJson = jsonMapper.serialize(arrayListFields);

        String expectedJson = """
                {
                  "targetClass": "inc.CollectionsSerdeTest$ArrayListFields",
                  "state": {
                    "strings": {"type": "java.util.ArrayList", "value": []}
                  },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_emptyArrayListFields() {
        String json = """
                {
                  "targetClass": "inc.CollectionsSerdeTest$ArrayListFields",
                  "state": {
                    "strings": {"type": "java.util.ArrayList", "value": []}
                  },
                  "__idRef": 1
                }""";

        ArrayListFields actualInstance = jsonMapper.deserialize(json, ArrayListFields.class);

        ArrayListFields expectedInstance = new ArrayListFields(new ArrayList<>());
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_HashSetFields() {
        Set<User> users = new HashSet<>();
        users.add(new User("Mike"));
        HashSetFields hashSetFields = new HashSetFields(users);

        String actualJson = jsonMapper.serialize(hashSetFields);

        String expectedJson = """
                {
                  "targetClass": "inc.CollectionsSerdeTest$HashSetFields",
                  "state": {
                    "strings": {
                      "type": "java.util.HashSet",
                      "value": [
                        {
                          "targetClass": "inc.CollectionsSerdeTest$User",
                          "state": {
                            "name": {"type": "java.lang.String", "value": "Mike"}
                          },
                          "__idRef": 2
                        }
                      ]
                    }
                  },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_HashSetFields() {
        String json = """
                {
                  "targetClass": "inc.CollectionsSerdeTest$HashSetFields",
                  "state": {
                    "strings": {
                      "type": "java.util.HashSet",
                      "value": [
                        {
                          "targetClass": "inc.CollectionsSerdeTest$User",
                          "state": {
                            "name": {"type": "java.lang.String", "value": "Mike"}
                          },
                          "__idRef": 2
                        }
                      ]
                    }
                  },
                  "__idRef": 1
                }""";

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
