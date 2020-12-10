package inc.evil.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JsonSerdeTest {

    private final JsonSerde jsonSerde = new JsonSerde();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithClassFields() {
        ClassLiteral literal = new ClassLiteral(ArrayList.class);

        String actualJson = jsonSerde.serialize(literal);

        String expectedJson = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$ClassLiteral",
                  "state": {
                    "targetClass": {"type": "java.lang.Class", "value": "java.util.ArrayList"}
                  },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectWithClassFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$ClassLiteral",
                  "state": {
                    "targetClass": {"type": "java.lang.Class", "value": "java.util.ArrayList"}
                  },
                  "__idRef": 1
                }""";

        ClassLiteral actualInstance = jsonSerde.deserialize(json, ClassLiteral.class);

        ClassLiteral expectedInstance = new ClassLiteral(ArrayList.class);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToDeserialize_objectWithFinalFields() {
        String json = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$FinalFieldsDummy",
                  "state" : {
                    "firstName" : {
                      "type" : "java.lang.String",
                      "value" : "Mike"
                    },
                    "lastName" : {
                      "type" : "java.lang.String",
                      "value" : "Smith"
                    }
                  },
                  "__idRef": 1
                }""";

        FinalFieldsDummy actualInstance = jsonSerde.deserialize(json, FinalFieldsDummy.class);

        assertEquals(new FinalFieldsDummy("Mike", "Smith"), actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithMultidimensionalArraysFields() {
        MultidimensionalArrays arrays = new MultidimensionalArrays(
                new boolean[][]{
                        new boolean[]{false, true},
                        new boolean[]{true, false, true},
                        new boolean[]{true, true},
                        new boolean[]{true, false, false, true},
                }
        );

        String actualJson = jsonSerde.serialize(arrays);

        String expectedJson = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$MultidimensionalArrays",
                  "state": {
                    "booleans": {
                      "type": "[[Z",
                      "value": [
                        {"type": "[Z", "value": [false, true]},
                        {"type": "[Z", "value": [true, false, true]},
                        {"type": "[Z", "value": [true, true]},
                        {"type": "[Z", "value": [true, false, false, true]}
                      ]
                    }
                  },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectWithMultidimensionalArraysFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$MultidimensionalArrays",
                  "state": {
                    "booleans": {
                      "type": "[[Z",
                      "value": [
                        {"type": "[Z", "value": [false, true]},
                        {"type": "[Z", "value": [true, false, true]},
                        {"type": "[Z", "value": [true, true]},
                        {"type": "[Z", "value": [true, false, false, true]}
                      ]
                    }
                  },
                  "__idRef": 1
                }""";

        MultidimensionalArrays actualInstance = jsonSerde.deserialize(json, MultidimensionalArrays.class);

        MultidimensionalArrays expectedInstance = new MultidimensionalArrays(
                new boolean[][]{
                        new boolean[]{false, true},
                        new boolean[]{true, false, true},
                        new boolean[]{true, true},
                        new boolean[]{true, false, false, true},
                }
        );
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerialize_objectWithNoFields() {
        NoFields noFields = new NoFields();

        String actualJson = jsonSerde.serialize(noFields);

        String expectedJson = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$NoFields",
                  "state" : { },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserialize_objectWithNoFields() {
        String json = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$NoFields",
                  "state" : { },
                  "__idRef": 1
                }""";

        NoFields actualInstance = jsonSerde.deserialize(json, NoFields.class);

        assertNotNull(actualInstance);
    }

    @Test
    public void shouldBeAbleToSerialize_objectWithPrimitiveArraysFields() {
        PrimitiveArrayDummy dummy = new PrimitiveArrayDummy(
                new boolean[]{true, false},
                new byte[]{1},
                new char[]{13},
                new short[]{2},
                new int[]{4},
                new long[]{5L},
                new float[]{6.5f},
                new double[]{7.5d}
        );

        String actualJson = jsonSerde.serialize(dummy);

        String expectedJson = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$PrimitiveArrayDummy",
                  "state": {
                    "booleanArray": {"type": "[Z", "value": [true, false]},
                    "byteArray":    {"type": "[B","value": [1]},
                    "charArray":    {"type": "[C","value": [13]},
                    "shortArray":   {"type": "[S","value": [2]},
                    "intArray":     {"type": "[I","value": [4]},
                    "longArray":    {"type": "[J","value": [5]},
                    "floatArray":   {"type": "[F", "value": [6.5]},
                    "doubleArray":  {"type": "[D","value": [7.5]}
                  },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserialize_objectWithPrimitiveArraysFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$PrimitiveArrayDummy",
                  "state": {
                    "booleanArray": {"type": "[Z", "value": [true, false]},
                    "byteArray":    {"type": "[B","value": [1]},
                    "charArray":    {"type": "[C","value": [13]},
                    "shortArray":   {"type": "[S","value": [2]},
                    "intArray":     {"type": "[I","value": [4]},
                    "longArray":    {"type": "[J","value": [5]},
                    "floatArray":   {"type": "[F", "value": [6.5]},
                    "doubleArray":  {"type": "[D","value": [7.5]}
                  },
                  "__idRef": 1
                }""";

        PrimitiveArrayDummy actualInstance = jsonSerde.deserialize(json, PrimitiveArrayDummy.class);

        PrimitiveArrayDummy expectedInstance = new PrimitiveArrayDummy(
                new boolean[]{true, false},
                new byte[]{1},
                new char[]{13},
                new short[]{2},
                new int[]{4},
                new long[]{5L},
                new float[]{6.5f},
                new double[]{7.5d}
        );
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeAndDeserializeHashMaps() {
        Map<String, StringDummy> map = new ConcurrentHashMap<>();
        map.put("one", new StringDummy("Mike", "Smith"));
        map.put("two", new StringDummy("John", "Doe"));
        MapDummy expectedMap = new MapDummy(map);

        String json = jsonSerde.serialize(expectedMap);
        MapDummy actualMap = jsonSerde.deserialize(json, MapDummy.class);

        assertEquals(expectedMap, actualMap);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithStringFields() {
        StringDummy dummy = new StringDummy("Mike", "Smith");

        String actualJson = jsonSerde.serialize(dummy);

        String expectedJson = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$StringDummy",
                  "state" : {
                    "firstName" : {
                      "type" : "java.lang.String",
                      "value" : "Mike"
                    },
                    "lastName" : {
                      "type" : "java.lang.String",
                      "value" : "Smith"
                    }
                  },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithArrayOfObjectsFields() {
        ArrayDummy dummy = new ArrayDummy(new StringDummy[]{new StringDummy("Mike", "Smith"), new StringDummy("Dennis", "Ritchie")});

        String actualJson = jsonSerde.serialize(dummy);

        String expectedJson = """
                {
                   "targetClass": "inc.evil.serde.JsonSerdeTest$ArrayDummy",
                   "__idRef": 1,
                   "state": {
                     "strings": {
                       "type": "[Linc.evil.serde.JsonSerdeTest$StringDummy;",
                       "value": [
                         {
                           "targetClass": "inc.evil.serde.JsonSerdeTest$StringDummy",
                           "__idRef": 2,
                           "state": {
                             "firstName": {
                               "type": "java.lang.String",
                               "value": "Mike"
                             },
                             "lastName": {
                               "type": "java.lang.String",
                               "value": "Smith"
                             }
                           }
                         },
                         {
                           "targetClass": "inc.evil.serde.JsonSerdeTest$StringDummy",
                           "__idRef": 3,
                           "state": {
                             "firstName": {
                               "type": "java.lang.String",
                               "value": "Dennis"
                             },
                             "lastName": {
                               "type": "java.lang.String",
                               "value": "Ritchie"
                             }
                           }
                         }
                       ]
                     }
                   }
                 }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectWithArrayOfObjectsFields() {
        String json = """
                {
                   "targetClass": "inc.evil.serde.JsonSerdeTest$ArrayDummy",
                   "__idRef": 1,
                   "state": {
                     "strings": {
                       "type": "[Linc.evil.serde.JsonSerdeTest$StringDummy;",
                       "value": [
                         {
                           "targetClass": "inc.evil.serde.JsonSerdeTest$StringDummy",
                           "__idRef": 2,
                           "state": {
                             "firstName": {
                               "type": "java.lang.String",
                               "value": "Mike"
                             },
                             "lastName": {
                               "type": "java.lang.String",
                               "value": "Smith"
                             }
                           }
                         },
                         {
                           "targetClass": "inc.evil.serde.JsonSerdeTest$StringDummy",
                           "__idRef": 3,
                           "state": {
                             "firstName": {
                               "type": "java.lang.String",
                               "value": "Dennis"
                             },
                             "lastName": {
                               "type": "java.lang.String",
                               "value": "Ritchie"
                             }
                           }
                         }
                       ]
                     }
                   }
                 }""";

        ArrayDummy actualObject = jsonSerde.deserialize(json, ArrayDummy.class);

        ArrayDummy expectedObject = new ArrayDummy(new StringDummy[]{new StringDummy("Mike", "Smith"), new StringDummy("Dennis", "Ritchie")});
        assertEquals(expectedObject, actualObject);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithPrimitiveFields() {
        PrimitivesDummy dummy = new PrimitivesDummy(
                true,
                (byte) 1,
                (char) 2,
                (short) 3,
                4,
                5L,
                6.5f,
                7.5D
        );

        String actualJson = jsonSerde.serialize(dummy);

        String expectedJson = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$PrimitivesDummy",
                  "state" : {
                    "booleanField" : true,
                    "byteField" : 1,
                    "charField" : 2,
                    "shortField" : 3,
                    "intField" : 4,
                    "longField" : 5,
                    "floatField" : 6.5,
                    "doubleField" : 7.5
                  },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectWithPrimitiveFields() {
        String json = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$PrimitivesDummy",
                  "state" : {
                    "booleanField" : true,
                    "byteField" : 1,
                    "charField" : 2,
                    "shortField" : 3,
                    "intField" : 4,
                    "longField" : 5,
                    "floatField" : 6.5,
                    "doubleField" : 7.5
                  },
                  "__idRef": 1
                }""";

        PrimitivesDummy actualObject = jsonSerde.deserialize(json, PrimitivesDummy.class);

        PrimitivesDummy expectedObject = new PrimitivesDummy(true, (byte) 1, (char) 2, (short) 3, 4, 5L, 6.5f, 7.5D);
        assertEquals(expectedObject, actualObject);
    }

    @Test
    public void shouldIgnoreStaticAndTransientFields_uponSerialization() {
        IgnoreStaticFields dummy = new IgnoreStaticFields("Mike");

        String actualJson = jsonSerde.serialize(dummy);

        String expectedJson = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$IgnoreStaticFields",
                  "state" : {
                    "name" : {
                      "type" : "java.lang.String",
                      "value" : "Mike"
                    },
                    "age" : 666
                  },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldIgnoreStaticAndTransientFields_uponDeserialization() {
        String json = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$IgnoreStaticFields",
                  "state" : {
                    "name" : {
                      "type" : "java.lang.String",
                      "value" : "Mike"
                    }
                  },
                  "__idRef": 1
                }""";

        IgnoreStaticFields actualObject = jsonSerde.deserialize(json, IgnoreStaticFields.class);

        IgnoreStaticFields expectedObject = new IgnoreStaticFields("Mike");
        assertEquals(expectedObject, actualObject);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithNestedObjects() {
        PojoDummy dummy = new PojoDummy(new StringDummy("Mike", "Smith"), 42);

        String actualJson = jsonSerde.serialize(dummy);

        String expectedJson = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$PojoDummy",
                  "__idRef": 1,
                  "state": {
                    "user": {
                      "type": "inc.evil.serde.JsonSerdeTest$StringDummy",
                      "value": {
                        "targetClass": "inc.evil.serde.JsonSerdeTest$StringDummy",
                        "__idRef": 2,
                        "state": {
                          "firstName": {
                            "type": "java.lang.String",
                            "value": "Mike"
                          },
                          "lastName": {
                            "type": "java.lang.String",
                            "value": "Smith"
                          }
                        }
                      }
                    },
                    "age": 42
                  }
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectWithNestedObjects() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$PojoDummy",
                  "__idRef": 1,
                  "state": {
                    "user": {
                      "type": "inc.evil.serde.JsonSerdeTest$StringDummy",
                      "value": {
                        "targetClass": "inc.evil.serde.JsonSerdeTest$StringDummy",
                        "__idRef": 2,
                        "state": {
                          "firstName": {
                            "type": "java.lang.String",
                            "value": "Mike"
                          },
                          "lastName": {
                            "type": "java.lang.String",
                            "value": "Smith"
                          }
                        }
                      }
                    },
                    "age": 42
                  }
                }""";

        PojoDummy actualObject = jsonSerde.deserialize(json, PojoDummy.class);

        PojoDummy expectedObject = new PojoDummy(new StringDummy("Mike", "Smith"), 42);
        assertEquals(expectedObject, actualObject);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithEnumFields() {
        EnumDummy dummy = new EnumDummy(EnumDummy.Season.SPRING, "Mike", null);

        String actualJson = jsonSerde.serialize(dummy);

        String expectedJson = """
                {
                   "targetClass": "inc.evil.serde.JsonSerdeTest$EnumDummy",
                   "state": {
                     "season": {"type": "inc.evil.serde.JsonSerdeTest$EnumDummy$Season", "value": "SPRING"},
                     "name": {"type": "java.lang.String", "value": "Mike"},
                     "nullableSeason": {"type": "inc.evil.serde.JsonSerdeTest$EnumDummy$Season", "value": null}
                   },
                   "__idRef": 1
                 }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectWithEnumFields() {
        String json = """
                 {
                   "targetClass": "inc.evil.serde.JsonSerdeTest$EnumDummy",
                   "state": {
                     "season": {"type": "inc.evil.serde.JsonSerdeTest$EnumDummy$Season", "value": "SPRING"},
                     "name": {"type": "java.lang.String", "value": "Mike"},
                     "nullableSeason": {"type": "inc.evil.serde.JsonSerdeTest$EnumDummy$Season", "value": null}
                   },
                   "__idRef": 1
                 }""";

        EnumDummy actualDeserializedInstance = jsonSerde.deserialize(json, EnumDummy.class);

        EnumDummy expectedInstance = new EnumDummy(EnumDummy.Season.SPRING, "Mike", null);
        assertEquals(actualDeserializedInstance, expectedInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithListFields() {
        ListDummy dummy = new ListDummy(List.of("Funky", "shit"));

        String actualJson = jsonSerde.serialize(dummy);

        String expectedJson = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$ListDummy",
                  "state" : {
                    "values" : [ "Funky", "shit" ]
                  },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectWithCircularDependencies() {
        String json = """
                {
                   "targetClass": "inc.evil.serde.JsonSerdeTest$CircularInstance1",
                   "state": {
                     "circularInstance2": {
                       "type": "inc.evil.serde.JsonSerdeTest$CircularInstance2",
                       "value": {
                         "targetClass": "inc.evil.serde.JsonSerdeTest$CircularInstance2",
                         "state": {
                           "circularInstance1": {
                             "type": "inc.evil.serde.JsonSerdeTest$CircularInstance1",
                             "value": {"type": "__ref", "value": "1"}
                           },
                           "name": {"type": "java.lang.String", "value": "Mike"}
                         },
                         "__idRef": 2
                       }
                     },
                     "age": 42
                   },
                   "__idRef": 1
                 }""";

        CircularInstance1 actualInstance = jsonSerde.deserialize(json, CircularInstance1.class);

        CircularInstance1 expectedInstance = new CircularInstance1();
        CircularInstance2 instance2 = new CircularInstance2(expectedInstance, "Mike");
        expectedInstance.age = 42;
        expectedInstance.circularInstance2 = instance2;

        assertEquals(actualInstance.age, expectedInstance.age);
        assertEquals(actualInstance.circularInstance2.name, expectedInstance.circularInstance2.name);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithCircularDependencies() {
        CircularInstance1 instance1 = new CircularInstance1();
        CircularInstance2 instance2 = new CircularInstance2(instance1, "Mike");
        instance1.age = 42;
        instance1.circularInstance2 = instance2;

        String actualJson = jsonSerde.serialize(instance1);

        String expectedJson = """
                {
                   "targetClass": "inc.evil.serde.JsonSerdeTest$CircularInstance1",
                   "state": {
                     "circularInstance2": {
                       "type": "inc.evil.serde.JsonSerdeTest$CircularInstance2",
                       "value": {
                         "targetClass": "inc.evil.serde.JsonSerdeTest$CircularInstance2",
                         "state": {
                           "circularInstance1": {
                             "type": "inc.evil.serde.JsonSerdeTest$CircularInstance1",
                             "value": {"type": "__ref", "value": "1"}
                           },
                           "name": {"type": "java.lang.String", "value": "Mike"}
                         },
                         "__idRef": 2
                       }
                     },
                     "age": 42
                   },
                   "__idRef": 1
                 }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectWithListFields() {
        String json = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$ListDummy",
                  "state" : {
                    "values" : [ "Funky", "shit" ]
                  },
                  "__idRef": 1
                }""";

        ListDummy actualDummy = jsonSerde.deserialize(json, ListDummy.class);

        ListDummy expectedDummy = new ListDummy(List.of("Funky", "shit"));
        assertEquals(expectedDummy, actualDummy);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectWithStringFields() {
        String json = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$StringDummy",
                  "state" : {
                    "firstName" : {
                      "type" : "java.lang.String",
                      "value" : "Mike"
                    },
                    "lastName" : {
                      "type" : "java.lang.String",
                      "value" : "Smith"
                    }
                  },
                  "__idRef": 1
                }""";

        StringDummy actualDeserializedInstance = jsonSerde.deserialize(json, StringDummy.class);

        assertEquals(new StringDummy("Mike", "Smith"), actualDeserializedInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectsWithSuperclasses() {
        InheritedFields inheritedFields = new InheritedFields("Mike", 29);

        String actualJson = jsonSerde.serialize(inheritedFields);

        String expectedJson = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$InheritedFields",
                  "state": {
                    "age": 29,
                    "name": {"type": "java.lang.String", "value": "Mike"}
                  },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectsWithSuperclasses() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$InheritedFields",
                  "state": {
                    "age": 29,
                    "name": {"type": "java.lang.String", "value": "Mike"}
                  },
                  "__idRef": 1
                }""";

        InheritedFields actualInstance = jsonSerde.deserialize(json, InheritedFields.class);

        InheritedFields expectedInstance = new InheritedFields("Mike", 29);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithObjectFields() {
        UpcastToObject upcastToObject = new UpcastToObject(String.class, "John");

        String actualJson = jsonSerde.serialize(upcastToObject);

        String expectedJson = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$UpcastToObject",
                  "state": {
                    "source": {"type": "java.lang.Class", "value": "java.lang.String"},
                    "name": {"type": "java.lang.String", "value": "John"}
                  },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectWithObjectFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$UpcastToObject",
                  "state": {
                    "source": {"type": "java.lang.Class", "value": "java.lang.String"},
                    "name": {"type": "java.lang.String", "value": "John"}
                  },
                  "__idRef": 1
                }""";

        UpcastToObject actualInstance = jsonSerde.deserialize(json, UpcastToObject.class);

        UpcastToObject expectedInstance = new UpcastToObject(String.class, "John");
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithDateFields() {
        Dates dates = new Dates(
                LocalDate.of(2020, Month.DECEMBER, 25),
                LocalDateTime.of(2020, Month.NOVEMBER, 21, 17, 25, 59),
                OffsetDateTime.of(2021, 5, 2, 14, 40, 50, 3, ZoneOffset.UTC),
                ZonedDateTime.of(LocalDateTime.of(2020, 11, 23, 13, 14, 45), ZoneId.of("Europe/Paris")));

        String actualJson = jsonSerde.serialize(dates);

        String expectedJson = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$Dates",
                  "state": {
                    "localDate": {"type": "java.time.LocalDate", "value": "2020-12-25"},
                    "localDateTime": {"type": "java.time.LocalDateTime", "value": "2020-11-21T17:25:59"},
                    "offsetDateTime": {"type": "java.time.OffsetDateTime", "value": "2021-05-02T14:40:50.000000003Z"},
                    "zonedDateTime": {"type": "java.time.ZonedDateTime", "value": "2020-11-23T13:14:45+01:00[Europe/Paris]"}
                  },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectWithDateFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$Dates",
                  "state": {
                    "localDate": {"type": "java.time.LocalDate", "value": "2020-12-25"},
                    "localDateTime": {"type": "java.time.LocalDateTime", "value": "2020-11-21T17:25:59"},
                    "offsetDateTime": {"type": "java.time.OffsetDateTime", "value": "2021-05-02T14:40:50.000000003Z"},
                    "zonedDateTime": {"type": "java.time.ZonedDateTime", "value": "2020-11-23T13:14:45+01:00[Europe/Paris]"}
                  },
                  "__idRef": 1
                }""";

        Dates actualInstance = jsonSerde.deserialize(json, Dates.class);

        Dates expectedInstance = new Dates(
                LocalDate.of(2020, Month.DECEMBER, 25),
                LocalDateTime.of(2020, Month.NOVEMBER, 21, 17, 25, 59),
                OffsetDateTime.of(2021, 5, 2, 14, 40, 50, 3, ZoneOffset.UTC),
                ZonedDateTime.of(LocalDateTime.of(2020, 11, 23, 13, 14, 45), ZoneId.of("Europe/Paris")));
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToHandleNullPrimitiveWrappers() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$NullWrappers",
                  "state": {
                    "age": 11123456789,
                    "id": null
                  },
                  "__idRef": 1
                }""";

        NullWrappers actualInstance = jsonSerde.deserialize(json, NullWrappers.class);

        NullWrappers expectedInstance = new NullWrappers(null, 11123456789L);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithAtomicIntegersFields() {
        AtomicNumbers atomicNumbers = new AtomicNumbers(
                new AtomicInteger(42),
                new AtomicLong(66)
        );

        String actualJson = jsonSerde.serialize(atomicNumbers);

        String expectedJson = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$AtomicNumbers",
                  "state" : {
                    "atomicInteger" : {"type" : "java.util.concurrent.atomic.AtomicInteger", "value" : 42},
                    "atomicLong" : {"type" : "java.util.concurrent.atomic.AtomicLong", "value" : 66}
                  },
                  "__idRef" : 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectWithAtomicIntegersFields() {
        String json = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$AtomicNumbers",
                  "state" : {
                    "atomicInteger" : {"type" : "java.util.concurrent.atomic.AtomicInteger", "value" : 42},
                    "atomicLong" : {"type" : "java.util.concurrent.atomic.AtomicLong", "value" : 66}
                  },
                  "__idRef" : 1
                }""";

        AtomicNumbers actualInstance = jsonSerde.deserialize(json, AtomicNumbers.class);

        AtomicNumbers expectedInstance = new AtomicNumbers(
                new AtomicInteger(42),
                new AtomicLong(66)
        );
        assertEquals(expectedInstance.atomicInteger.get(), actualInstance.atomicInteger.get());
        assertEquals(expectedInstance.atomicLong.get(), actualInstance.atomicLong.get());
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithNullDatesFields() {
        NullDates nullDates = new NullDates(null, null, null);

        String actualJson = jsonSerde.serialize(nullDates);

        String expectedJson = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$NullDates",
                  "state": {
                    "localDate": {"type": "java.time.LocalDate", "value": null},
                    "localDateTime": {"type": "java.time.LocalDateTime", "value": null},
                    "offsetDateTime": {"type": "java.time.OffsetDateTime", "value": null}
                  },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectWithNullDatesFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$NullDates",
                  "state": {
                    "localDate": {"type": "java.time.LocalDate", "value": null},
                    "localDateTime": {"type": "java.time.LocalDateTime", "value": null},
                    "offsetDateTime": {"type": "java.time.OffsetDateTime", "value": null}
                  },
                  "__idRef": 1
                }""";

        NullDates actualInstance = jsonSerde.deserialize(json, NullDates.class);

        NullDates expectedInstance = new NullDates(null, null, null);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectWithBigNumbersFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$BigNumbers",
                  "state": {
                    "bigDecimal": {
                      "type": "java.math.BigDecimal",
                      "value": "66612334556798765645435342334357676863343434354672234566.69"
                    },
                    "bigInteger": {
                      "type": "java.math.BigInteger",
                      "value": "123445678990766"
                    }
                  },
                  "__idRef": 1
                }""";

        BigNumbers actualInstance = jsonSerde.deserialize(json, BigNumbers.class);

        BigNumbers expectedInstance = new BigNumbers(
                new BigDecimal("66612334556798765645435342334357676863343434354672234566.69"),
                new BigInteger("123445678990766")
        );
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithBigNumbersFields() {
        BigNumbers bigNumbers = new BigNumbers(
                new BigDecimal("66612334556798765645435342334357676863343434354672234566.69"),
                new BigInteger("123445678990766")
        );

        String actualJson = jsonSerde.serialize(bigNumbers);

        String expectedJson = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$BigNumbers",
                  "state": {
                    "bigDecimal": {
                      "type": "java.math.BigDecimal",
                      "value": "66612334556798765645435342334357676863343434354672234566.69"
                    },
                    "bigInteger": {
                      "type": "java.math.BigInteger",
                      "value": "123445678990766"
                    }
                  },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithPeriodsAndDurationFields() {
        PeriodsAndDurations periodsAndDurations = new PeriodsAndDurations(
                Period.ofDays(1),
                Duration.ofMinutes(2)
        );

        String actualJson = jsonSerde.serialize(periodsAndDurations);

        String expectedJson = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$PeriodsAndDurations",
                  "state": {
                    "period": {"type": "java.time.Period", "value": "P1D"},
                    "duration": {"type": "java.time.Duration", "value": "PT2M"}
                  },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectWithPeriodsAndDurationFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$PeriodsAndDurations",
                  "state": {
                    "period": {"type": "java.time.Period", "value": "P1D"},
                    "duration": {"type": "java.time.Duration", "value": "PT2M"}
                  },
                  "__idRef": 1
                }""";

        PeriodsAndDurations actualInstance = jsonSerde.deserialize(json, PeriodsAndDurations.class);

        PeriodsAndDurations expectedInstance = new PeriodsAndDurations(
                Period.ofDays(1),
                Duration.ofMinutes(2)
        );
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithNullArraysFields() {
        NullArrays nullArrays = new NullArrays(null, null);

        String actualJson = jsonSerde.serialize(nullArrays);

        String expectedJson = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$NullArrays",
                  "state": {
                    "bytes": null,
                    "strings": null
                  },
                  "__idRef": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectWithNullArraysFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$NullArrays",
                  "state": {
                    "bytes": null,
                    "strings": null
                  },
                  "__idRef": 1
                }""";

        NullArrays actualInstance = jsonSerde.deserialize(json, NullArrays.class);

        NullArrays expectedInstance = new NullArrays(null, null);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithNullInterfaceFields() {
        NullInterfaceFields nullInterfaceFields = new NullInterfaceFields(null);

        String actualJson = jsonSerde.serialize(nullInterfaceFields);

        String expectedJson = """
                {
                   "targetClass": "inc.evil.serde.JsonSerdeTest$NullInterfaceFields",
                   "state": {
                     "strings": null
                   },
                   "__idRef": 1
                 }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeToJson_objectWithNullInterfaceFields() {
        String json = """
                {
                   "targetClass": "inc.evil.serde.JsonSerdeTest$NullInterfaceFields",
                   "state": {
                     "strings": null
                   },
                   "__idRef": 1
                 }""";

        NullInterfaceFields actualInstance = jsonSerde.deserialize(json, NullInterfaceFields.class);

        NullInterfaceFields expectedInstance = new NullInterfaceFields(null);
        assertEquals(expectedInstance, actualInstance);
    }

    private void assertJsonEquals(String expectedJson, String actualJson) {
        try {
            assertEquals(objectMapper.readTree(expectedJson), objectMapper.readTree(actualJson));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static class NullInterfaceFields {
        private List<String> strings;

        public NullInterfaceFields(List<String> strings) {
            this.strings = strings;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NullInterfaceFields that = (NullInterfaceFields) o;

            return strings != null ? strings.equals(that.strings) : that.strings == null;
        }

        @Override
        public int hashCode() {
            return strings != null ? strings.hashCode() : 0;
        }
    }

    public static class NullArrays {
        private final byte[] bytes;
        private final StringDummy[] strings;

        public NullArrays(byte[] bytes, StringDummy[] strings) {
            this.bytes = bytes;
            this.strings = strings;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NullArrays that = (NullArrays) o;

            if (!Arrays.equals(bytes, that.bytes)) return false;
            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(strings, that.strings);
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(bytes);
            result = 31 * result + Arrays.hashCode(strings);
            return result;
        }
    }

    public static class PeriodsAndDurations {
        private final Period period;
        private final Duration duration;

        public PeriodsAndDurations(Period period, Duration duration) {
            this.period = period;
            this.duration = duration;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PeriodsAndDurations that = (PeriodsAndDurations) o;

            if (period != null ? !period.equals(that.period) : that.period != null) return false;
            return duration != null ? duration.equals(that.duration) : that.duration == null;
        }

        @Override
        public int hashCode() {
            int result = period != null ? period.hashCode() : 0;
            result = 31 * result + (duration != null ? duration.hashCode() : 0);
            return result;
        }
    }

    public static class BigNumbers {
        private final BigDecimal bigDecimal;
        private final BigInteger bigInteger;

        public BigNumbers(BigDecimal bigDecimal, BigInteger bigInteger) {
            this.bigDecimal = bigDecimal;
            this.bigInteger = bigInteger;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BigNumbers that = (BigNumbers) o;

            if (bigDecimal != null ? !bigDecimal.equals(that.bigDecimal) : that.bigDecimal != null) return false;
            return bigInteger != null ? bigInteger.equals(that.bigInteger) : that.bigInteger == null;
        }

        @Override
        public int hashCode() {
            int result = bigDecimal != null ? bigDecimal.hashCode() : 0;
            result = 31 * result + (bigInteger != null ? bigInteger.hashCode() : 0);
            return result;
        }
    }

    public static class NullWrappers {
        private final Long id;
        private final long age;

        public NullWrappers(Long id, long age) {
            this.id = id;
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NullWrappers that = (NullWrappers) o;

            if (age != that.age) return false;
            return id != null ? id.equals(that.id) : that.id == null;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (int) age;
            return result;
        }

        @Override
        public String toString() {
            return "NullWrappers{" +
                    "id=" + id +
                    ", age=" + age +
                    '}';
        }
    }

    public static class AtomicNumbers {
        private final AtomicInteger atomicInteger;
        private final AtomicLong atomicLong;

        public AtomicNumbers(AtomicInteger atomicInteger, AtomicLong atomicLong) {
            this.atomicInteger = atomicInteger;
            this.atomicLong = atomicLong;
        }

        @Override
        public String toString() {
            return "AtomicNumbers{" +
                    "atomicInteger=" + atomicInteger +
                    ", atomicLong=" + atomicLong +
                    '}';
        }
    }

    public static class NullDates {
        private final LocalDate localDate;
        private final LocalDateTime localDateTime;
        private final OffsetDateTime offsetDateTime;

        public NullDates(LocalDate localDate, LocalDateTime localDateTime, OffsetDateTime offsetDateTime) {
            this.localDate = localDate;
            this.localDateTime = localDateTime;
            this.offsetDateTime = offsetDateTime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NullDates nullDates = (NullDates) o;

            if (localDate != null ? !localDate.equals(nullDates.localDate) : nullDates.localDate != null) return false;
            if (localDateTime != null ? !localDateTime.equals(nullDates.localDateTime) : nullDates.localDateTime != null)
                return false;
            return offsetDateTime != null ? offsetDateTime.equals(nullDates.offsetDateTime) : nullDates.offsetDateTime == null;
        }

        @Override
        public int hashCode() {
            int result = localDate != null ? localDate.hashCode() : 0;
            result = 31 * result + (localDateTime != null ? localDateTime.hashCode() : 0);
            result = 31 * result + (offsetDateTime != null ? offsetDateTime.hashCode() : 0);
            return result;
        }
    }

    public static class Dates {
        private final LocalDate localDate;
        private final LocalDateTime localDateTime;
        private final OffsetDateTime offsetDateTime;
        private final ZonedDateTime zonedDateTime;

        public Dates(LocalDate localDate, LocalDateTime localDateTime, OffsetDateTime offsetDateTime, ZonedDateTime zonedDateTime) {
            this.localDate = localDate;
            this.localDateTime = localDateTime;
            this.offsetDateTime = offsetDateTime;
            this.zonedDateTime = zonedDateTime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Dates dates = (Dates) o;

            if (localDate != null ? !localDate.equals(dates.localDate) : dates.localDate != null) return false;
            if (localDateTime != null ? !localDateTime.equals(dates.localDateTime) : dates.localDateTime != null)
                return false;
            if (offsetDateTime != null ? !offsetDateTime.equals(dates.offsetDateTime) : dates.offsetDateTime != null)
                return false;
            return zonedDateTime != null ? zonedDateTime.equals(dates.zonedDateTime) : dates.zonedDateTime == null;
        }

        @Override
        public int hashCode() {
            int result = localDate != null ? localDate.hashCode() : 0;
            result = 31 * result + (localDateTime != null ? localDateTime.hashCode() : 0);
            result = 31 * result + (offsetDateTime != null ? offsetDateTime.hashCode() : 0);
            result = 31 * result + (zonedDateTime != null ? zonedDateTime.hashCode() : 0);
            return result;
        }
    }

    public static class UpcastToObject {
        private final Object source;
        private final String name;

        public UpcastToObject(Object source, String name) {
            this.source = source;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UpcastToObject that = (UpcastToObject) o;

            if (source != null ? !source.equals(that.source) : that.source != null) return false;
            return name != null ? name.equals(that.name) : that.name == null;
        }

        @Override
        public int hashCode() {
            int result = source != null ? source.hashCode() : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }

    public static class ClassLiteral {
        private Class<?> targetClass;

        public ClassLiteral(Class<?> targetClass) {
            this.targetClass = targetClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClassLiteral that = (ClassLiteral) o;

            return targetClass != null ? targetClass.equals(that.targetClass) : that.targetClass == null;
        }

        @Override
        public int hashCode() {
            return targetClass != null ? targetClass.hashCode() : 0;
        }
    }

    public static class CircularInstance1 {
        private CircularInstance2 circularInstance2;
        private int age;

        public CircularInstance1() {
            this.age = age;
        }

        public void setCircularInstance2(CircularInstance2 circularInstance2) {
            this.circularInstance2 = circularInstance2;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public static class Named {
        protected String name;

        public Named(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class InheritedFields extends Named {
        private int age;

        public InheritedFields(String name, int age) {
            super(name);
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            InheritedFields that = (InheritedFields) o;

            return age == that.age && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(age, name);
        }
    }

    public static class CircularInstance2 {
        private final CircularInstance1 circularInstance1;
        private final String name;

        public CircularInstance2(CircularInstance1 circularInstance1, String name) {
            this.circularInstance1 = circularInstance1;
            this.name = name;
        }
    }

    public static class MultidimensionalArrays {
        private boolean[][] booleans;

        public MultidimensionalArrays(boolean[][] booleans) {
            this.booleans = booleans;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MultidimensionalArrays that = (MultidimensionalArrays) o;

            return Arrays.deepEquals(booleans, that.booleans);
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(booleans);
        }

        @Override
        public String toString() {
            return "MultidimensionalArrays{" +
                    "booleans=" + Arrays.toString(booleans) +
                    '}';
        }
    }

    public static class MapDummy {
        private Map<String, StringDummy> map;

        public MapDummy(Map<String, StringDummy> map) {
            this.map = map;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MapDummy mapDummy = (MapDummy) o;

            return map != null ? map.equals(mapDummy.map) : mapDummy.map == null;
        }

        @Override
        public int hashCode() {
            return map != null ? map.hashCode() : 0;
        }
    }

    public static class NoFields {
    }

    public static class FinalFieldsDummy {
        private final String firstName;
        private final String lastName;

        public FinalFieldsDummy(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FinalFieldsDummy that = (FinalFieldsDummy) o;

            if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
            return lastName != null ? lastName.equals(that.lastName) : that.lastName == null;
        }

        @Override
        public int hashCode() {
            int result = firstName != null ? firstName.hashCode() : 0;
            result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
            return result;
        }
    }

    public static class StringDummy {

        private String firstName;
        private String lastName;

        public StringDummy() {
        }

        public StringDummy(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StringDummy dummy = (StringDummy) o;

            if (firstName != null ? !firstName.equals(dummy.firstName) : dummy.firstName != null) return false;
            return lastName != null ? lastName.equals(dummy.lastName) : dummy.lastName == null;
        }

        @Override
        public int hashCode() {
            int result = firstName != null ? firstName.hashCode() : 0;
            result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "StringDummy{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    '}';
        }
    }

    public static class PrimitiveArrayDummy {
        boolean[] booleanArray;
        byte[] byteArray;
        char[] charArray;
        short[] shortArray;
        int[] intArray;
        long[] longArray;
        float[] floatArray;
        double[] doubleArray;

        public PrimitiveArrayDummy(boolean[] booleanArray, byte[] byteArray, char[] charArray, short[] shortArray, int[] intArray, long[] longArray, float[] floatArray, double[] doubleArray) {
            this.booleanArray = booleanArray;
            this.byteArray = byteArray;
            this.charArray = charArray;
            this.shortArray = shortArray;
            this.intArray = intArray;
            this.longArray = longArray;
            this.floatArray = floatArray;
            this.doubleArray = doubleArray;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PrimitiveArrayDummy that = (PrimitiveArrayDummy) o;

            if (!Arrays.equals(booleanArray, that.booleanArray)) return false;
            if (!Arrays.equals(byteArray, that.byteArray)) return false;
            if (!Arrays.equals(charArray, that.charArray)) return false;
            if (!Arrays.equals(shortArray, that.shortArray)) return false;
            if (!Arrays.equals(intArray, that.intArray)) return false;
            if (!Arrays.equals(longArray, that.longArray)) return false;
            if (!Arrays.equals(floatArray, that.floatArray)) return false;
            return Arrays.equals(doubleArray, that.doubleArray);
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(booleanArray);
            result = 31 * result + Arrays.hashCode(byteArray);
            result = 31 * result + Arrays.hashCode(charArray);
            result = 31 * result + Arrays.hashCode(shortArray);
            result = 31 * result + Arrays.hashCode(intArray);
            result = 31 * result + Arrays.hashCode(longArray);
            result = 31 * result + Arrays.hashCode(floatArray);
            result = 31 * result + Arrays.hashCode(doubleArray);
            return result;
        }
    }

    public static class ArrayDummy {
        private StringDummy[] strings;

        public ArrayDummy(StringDummy[] strings) {
            this.strings = strings;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ArrayDummy that = (ArrayDummy) o;

            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(strings, that.strings);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(strings);
        }

        @Override
        public String toString() {
            return "ArrayDummy{" +
                    "strings=" + Arrays.toString(strings) +
                    '}';
        }
    }

    public static class PojoDummy {
        private StringDummy user;
        private int age;

        public PojoDummy(StringDummy user, int age) {
            this.user = user;
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PojoDummy pojoDummy = (PojoDummy) o;

            if (age != pojoDummy.age) return false;
            return user != null ? user.equals(pojoDummy.user) : pojoDummy.user == null;
        }

        @Override
        public int hashCode() {
            int result = user != null ? user.hashCode() : 0;
            result = 31 * result + age;
            return result;
        }

        @Override
        public String toString() {
            return "PojoDummy{" +
                    "user=" + user +
                    ", age=" + age +
                    '}';
        }
    }

    public static class ListDummy {
        private List<String> values;

        public ListDummy() {
        }

        public ListDummy(List<String> values) {
            this.values = values;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ListDummy listDummy = (ListDummy) o;

            return values != null ? values.equals(listDummy.values) : listDummy.values == null;
        }

        @Override
        public int hashCode() {
            return values != null ? values.hashCode() : 0;
        }
    }

    public static class EnumDummy {
        enum Season {SPRING, WINTER}

        private Season season;
        private String name;
        private Season nullableSeason;

        public EnumDummy(Season season, String name, Season nullableSeason) {
            this.season = season;
            this.name = name;
            this.nullableSeason = nullableSeason;
        }

        public EnumDummy() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EnumDummy enumDummy = (EnumDummy) o;

            if (season != enumDummy.season) return false;
            if (name != null ? !name.equals(enumDummy.name) : enumDummy.name != null) return false;
            return nullableSeason == enumDummy.nullableSeason;
        }

        @Override
        public int hashCode() {
            int result = season != null ? season.hashCode() : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + (nullableSeason != null ? nullableSeason.hashCode() : 0);
            return result;
        }
    }

    public static class IgnoreStaticFields {
        static int id = 42;

        transient int age = 666;

        private String name;

        public IgnoreStaticFields(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IgnoreStaticFields that = (IgnoreStaticFields) o;

            return name != null ? name.equals(that.name) : that.name == null;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "IgnoreStaticFields{" +
                    "id=" + id +
                    ", age=" + age +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public static class PrimitivesDummy {
        private boolean booleanField;
        private byte byteField;
        private char charField;
        private short shortField;
        private int intField;
        private long longField;
        private float floatField;
        private double doubleField;

        public PrimitivesDummy(boolean booleanField, byte byteField, char charField, short shortField, int intField, long longField, float floatField, double doubleField) {
            this.booleanField = booleanField;
            this.byteField = byteField;
            this.charField = charField;
            this.shortField = shortField;
            this.intField = intField;
            this.longField = longField;
            this.floatField = floatField;
            this.doubleField = doubleField;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PrimitivesDummy that = (PrimitivesDummy) o;

            if (booleanField != that.booleanField) return false;
            if (byteField != that.byteField) return false;
            if (charField != that.charField) return false;
            if (shortField != that.shortField) return false;
            if (intField != that.intField) return false;
            if (longField != that.longField) return false;
            if (Float.compare(that.floatField, floatField) != 0) return false;
            return Double.compare(that.doubleField, doubleField) == 0;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = (booleanField ? 1 : 0);
            result = 31 * result + (int) byteField;
            result = 31 * result + (int) charField;
            result = 31 * result + (int) shortField;
            result = 31 * result + intField;
            result = 31 * result + (int) (longField ^ (longField >>> 32));
            result = 31 * result + (floatField != +0.0f ? Float.floatToIntBits(floatField) : 0);
            temp = Double.doubleToLongBits(doubleField);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }
}
