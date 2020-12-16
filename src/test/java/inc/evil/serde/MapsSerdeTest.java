package inc.evil.serde;

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
        MapFields mapFields = new MapFields(users, null);

        String actualJson = jsonMapper.serialize(mapFields);

        String expectedJson = """
                {
                    "targetClass": "inc.evil.serde.MapsSerdeTest$MapFields",
                    "__id": 1,
                    "state": {
                      "users": {
                        "type": "java.util.HashMap",
                        "value": {
                          "type": "java.util.HashMap",
                          "value": [
                            {
                              "key": "Mike",
                              "value": {
                                "targetClass": "inc.evil.serde.MapsSerdeTest$User",
                                "__id": 2,
                                "state": {
                                  "name": {"type": "java.lang.String", "value": "Mike"}
                                }
                              }
                            },
                            {
                              "key": "John",
                              "value": {
                                "targetClass": "inc.evil.serde.MapsSerdeTest$User",
                                "__id": 3,
                                "state": {
                                  "name": {"type": "java.lang.String", "value": "John"}
                                }
                              }
                            }
                          ]
                        }
                      },
                      "moreUsers": {
                        "type": "java.util.Map",
                        "value": null
                      }
                    }
                  }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithHashMapFields() {
        String json = """
                {
                    "targetClass": "inc.evil.serde.MapsSerdeTest$MapFields",
                    "__id": 1,
                    "state": {
                      "users": {
                        "type": "java.util.HashMap",
                        "value": {
                          "type": "java.util.HashMap",
                          "value": [
                            {
                              "key": "Mike",
                              "value": {
                                "targetClass": "inc.evil.serde.MapsSerdeTest$User",
                                "__id": 2,
                                "state": {
                                  "name": {"type": "java.lang.String", "value": "Mike"}
                                }
                              }
                            },
                            {
                              "key": "John",
                              "value": {
                                "targetClass": "inc.evil.serde.MapsSerdeTest$User",
                                "__id": 3,
                                "state": {
                                  "name": {"type": "java.lang.String", "value": "John"}
                                }
                              }
                            }
                          ]
                        }
                      },
                      "moreUsers": {
                        "type": "java.util.Map",
                        "value": null
                      }
                    }
                  }""";

        MapFields actualInstance = jsonMapper.deserialize(json, MapFields.class);

        Map<String, User> users = new HashMap<>();
        users.put("Mike", new User("Mike"));
        users.put("John", new User("John"));
        MapFields expectedInstance = new MapFields(users, null);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithHashMapFieldsWithObjectKeys() {
        Map<User, String> users = new HashMap<>();
        users.put(new User("Mike"), "Mike");
        users.put(new User("John"), "John");
        MapFields mapFields = new MapFields(null, users);

        String actualJson = jsonMapper.serialize(mapFields);

        String expectedJson = """
                {
                   "targetClass": "inc.evil.serde.MapsSerdeTest$MapFields",
                   "__id": 1,
                   "state": {
                     "users": {
                       "type": "java.util.Map",
                       "value": null
                     },
                     "moreUsers": {
                       "type": "java.util.HashMap",
                       "value": {
                         "type": "java.util.HashMap",
                         "value": [
                           {
                             "key": {
                               "targetClass": "inc.evil.serde.MapsSerdeTest$User",
                               "__id": 2,
                               "state": {
                                 "name": {"type": "java.lang.String", "value": "Mike"}
                               }
                             },
                             "value": "Mike"
                           },
                           {
                             "key": {
                               "targetClass": "inc.evil.serde.MapsSerdeTest$User",
                               "__id": 3,
                               "state": {
                                 "name": {"type": "java.lang.String", "value": "John"}
                               }
                             },
                             "value": "John"
                           }
                         ]
                       }
                     }
                   }
                 }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithHashMapFieldsWithObjectKeys() {
        String json = """
                {
                   "targetClass": "inc.evil.serde.MapsSerdeTest$MapFields",
                   "__id": 1,
                   "state": {
                     "users": {
                       "type": "java.util.Map",
                       "value": null
                     },
                     "moreUsers": {
                       "type": "java.util.HashMap",
                       "value": {
                         "type": "java.util.HashMap",
                         "value": [
                           {
                             "key": {
                               "targetClass": "inc.evil.serde.MapsSerdeTest$User",
                               "__id": 2,
                               "state": {
                                 "name": {"type": "java.lang.String", "value": "Mike"}
                               }
                             },
                             "value": "Mike"
                           },
                           {
                             "key": {
                               "targetClass": "inc.evil.serde.MapsSerdeTest$User",
                               "__id": 3,
                               "state": {
                                 "name": {"type": "java.lang.String", "value": "John"}
                               }
                             },
                             "value": "John"
                           }
                         ]
                       }
                     }
                   }
                 }""";

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
