package inc.evil.serde;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static inc.evil.serde.util.TestUtils.assertJsonEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JsonSerdeTest {

    private final JsonSerde jsonSerde = new JsonSerde();

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
                  "__id": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithClassFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$ClassLiteral",
                  "state": {
                    "targetClass": {"type": "java.lang.Class", "value": "java.util.ArrayList"}
                  },
                  "__id": 1
                }""";

        ClassLiteral actualInstance = jsonSerde.deserialize(json, ClassLiteral.class);

        ClassLiteral expectedInstance = new ClassLiteral(ArrayList.class);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleFromDeserialize_objectWithFinalFields() {
        String json = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$FinalFields",
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
                  "__id": 1
                }""";

        FinalFields actualInstance = jsonSerde.deserialize(json, FinalFields.class);

        assertEquals(new FinalFields("Mike", "Smith"), actualInstance);
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
                  "__id": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithMultidimensionalArraysFields() {
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
                  "__id": 1
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
                  "__id": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserialize_objectWithNoFields() {
        String json = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$NoFields",
                  "state" : { },
                  "__id": 1
                }""";

        NoFields actualInstance = jsonSerde.deserialize(json, NoFields.class);

        assertNotNull(actualInstance);
    }

    @Test
    public void shouldBeAbleToSerialize_objectWithPrimitiveArraysFields() {
        PrimitiveArrays dummy = new PrimitiveArrays(
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
                  "targetClass": "inc.evil.serde.JsonSerdeTest$PrimitiveArrays",
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
                  "__id": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserialize_objectWithPrimitiveArraysFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$PrimitiveArrays",
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
                  "__id": 1
                }""";

        PrimitiveArrays actualInstance = jsonSerde.deserialize(json, PrimitiveArrays.class);

        PrimitiveArrays expectedInstance = new PrimitiveArrays(
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
        Map<String, StringFields> map = new ConcurrentHashMap<>();
        map.put("one", new StringFields("Mike", "Smith"));
        map.put("two", new StringFields("John", "Doe"));
        MapFields expectedMap = new MapFields(map);

        String json = jsonSerde.serialize(expectedMap);
        MapFields actualMap = jsonSerde.deserialize(json, MapFields.class);

        assertEquals(expectedMap, actualMap);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithStringFields() {
        StringFields dummy = new StringFields("Mike", "Smith");

        String actualJson = jsonSerde.serialize(dummy);

        String expectedJson = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$StringFields",
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
                  "__id": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithArrayFields() {
        PojoArrays dummy = new PojoArrays(new StringFields[]{new StringFields("Mike", "Smith"), new StringFields("Dennis", "Ritchie")});

        String actualJson = jsonSerde.serialize(dummy);

        String expectedJson = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$PojoArrays",
                  "state": {
                    "strings": {
                      "type": "[Linc.evil.serde.JsonSerdeTest$StringFields;",
                      "value": [
                        {
                          "type": "inc.evil.serde.JsonSerdeTest$StringFields",
                          "value": {
                            "targetClass": "inc.evil.serde.JsonSerdeTest$StringFields",
                            "state": {
                              "firstName": {"type": "java.lang.String", "value": "Mike"},
                              "lastName": {"type": "java.lang.String", "value": "Smith"}},
                            "__id": 2
                          }
                        },
                        {
                          "type": "inc.evil.serde.JsonSerdeTest$StringFields",
                          "value": {
                            "targetClass": "inc.evil.serde.JsonSerdeTest$StringFields",
                            "state": {
                              "firstName": {"type": "java.lang.String", "value": "Dennis"},
                              "lastName": {"type": "java.lang.String", "value": "Ritchie"}},
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
    public void shouldBeAbleToDeserializeFromJson_objectWithArrayOfObjectsFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$PojoArrays",
                  "state": {
                    "strings": {
                      "type": "[Linc.evil.serde.JsonSerdeTest$StringFields;",
                      "value": [
                        {
                          "type": "inc.evil.serde.JsonSerdeTest$StringFields",
                          "value": {
                            "targetClass": "inc.evil.serde.JsonSerdeTest$StringFields",
                            "state": {
                              "firstName": {"type": "java.lang.String", "value": "Mike"},
                              "lastName": {"type": "java.lang.String", "value": "Smith"}},
                            "__id": 2
                          }
                        },
                        {
                          "type": "inc.evil.serde.JsonSerdeTest$StringFields",
                          "value": {
                            "targetClass": "inc.evil.serde.JsonSerdeTest$StringFields",
                            "state": {
                              "firstName": {"type": "java.lang.String", "value": "Dennis"},
                              "lastName": {"type": "java.lang.String", "value": "Ritchie"}},
                            "__id": 3
                          }
                        }
                      ]
                    }
                  },
                  "__id": 1
                }""";

        PojoArrays actualObject = jsonSerde.deserialize(json, PojoArrays.class);

        PojoArrays expectedObject = new PojoArrays(new StringFields[]{new StringFields("Mike", "Smith"), new StringFields("Dennis", "Ritchie")});
        assertEquals(expectedObject, actualObject);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithPrimitiveFields() {
        PrimitivesTypes dummy = new PrimitivesTypes(
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
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$PrimitivesTypes",
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
                  "__id": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithPrimitiveFields() {
        String json = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$PrimitivesTypes",
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
                  "__id": 1
                }""";

        PrimitivesTypes actualObject = jsonSerde.deserialize(json, PrimitivesTypes.class);

        PrimitivesTypes expectedObject = new PrimitivesTypes(true, (byte) 1, (char) 2, (short) 3, 4, 5L, 6.5f, 7.5D);
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
                  "__id": 1
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
                  "__id": 1
                }""";

        IgnoreStaticFields actualObject = jsonSerde.deserialize(json, IgnoreStaticFields.class);

        IgnoreStaticFields expectedObject = new IgnoreStaticFields("Mike");
        assertEquals(expectedObject, actualObject);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithNestedObjects() {
        PojoFields dummy = new PojoFields(new StringFields("Mike", "Smith"), 42);

        String actualJson = jsonSerde.serialize(dummy);

        String expectedJson = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$PojoFields",
                  "__id": 1,
                  "state": {
                    "user": {
                      "type": "inc.evil.serde.JsonSerdeTest$StringFields",
                      "value": {
                        "targetClass": "inc.evil.serde.JsonSerdeTest$StringFields",
                        "__id": 2,
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
    public void shouldBeAbleToDeserializeFromJson_objectWithNestedObjects() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$PojoFields",
                  "__id": 1,
                  "state": {
                    "user": {
                      "type": "inc.evil.serde.JsonSerdeTest$StringFields",
                      "value": {
                        "targetClass": "inc.evil.serde.JsonSerdeTest$StringFields",
                        "__id": 2,
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

        PojoFields actualObject = jsonSerde.deserialize(json, PojoFields.class);

        PojoFields expectedObject = new PojoFields(new StringFields("Mike", "Smith"), 42);
        assertEquals(expectedObject, actualObject);
    }

    @Test
    public void shouldBeAbleToSerializeFromJson_objectWithEnumFields() {
        EnumFieldValues dummy = new EnumFieldValues(EnumFieldValues.Season.SPRING, "Mike", null);

        String actualJson = jsonSerde.serialize(dummy);

        String expectedJson = """
                {
                   "targetClass": "inc.evil.serde.JsonSerdeTest$EnumFieldValues",
                   "__id": 1,
                   "state": {
                     "season": {
                       "type": "inc.evil.serde.JsonSerdeTest$EnumFieldValues$Season",
                       "value": {"type": "inc.evil.serde.JsonSerdeTest$EnumFieldValues$Season", "value": "SPRING"}
                     },
                     "name": {"type": "java.lang.String", "value": "Mike"},
                     "nullableSeason": {"type": "inc.evil.serde.JsonSerdeTest$EnumFieldValues$Season", "value": null}
                   }
                 }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithEnumFields() {
        String json = """
                {
                   "targetClass": "inc.evil.serde.JsonSerdeTest$EnumFieldValues",
                   "__id": 1,
                   "state": {
                     "season": {
                       "type": "inc.evil.serde.JsonSerdeTest$EnumFieldValues$Season",
                       "value": {"type": "inc.evil.serde.JsonSerdeTest$EnumFieldValues$Season", "value": "SPRING"}
                     },
                     "name": {"type": "java.lang.String", "value": "Mike"},
                     "nullableSeason": {"type": "inc.evil.serde.JsonSerdeTest$EnumFieldValues$Season", "value": null}
                   }
                 }""";

        EnumFieldValues actualDeserializedInstance = jsonSerde.deserialize(json, EnumFieldValues.class);

        EnumFieldValues expectedInstance = new EnumFieldValues(EnumFieldValues.Season.SPRING, "Mike", null);
        assertEquals(actualDeserializedInstance, expectedInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithListFields() {
        ListOfStrings dummy = new ListOfStrings(List.of("Funky", "shit"));

        String actualJson = jsonSerde.serialize(dummy);

        String expectedJson = """
                {
                   "targetClass": "inc.evil.serde.JsonSerdeTest$ListOfStrings",
                   "state": {
                     "values": {
                       "type": "java.util.ImmutableCollections$List12",
                       "value": {
                         "targetClass": "java.util.ImmutableCollections$List12",
                         "state": {
                           "e0": {"type": "java.lang.String", "value": "Funky"},
                           "e1": {"type": "java.lang.String", "value": "shit"}
                         },
                         "__id": 2
                       }
                     }
                   },
                   "__id": 1
                 }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithCircularDependencies() {
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
                         "__id": 2
                       }
                     },
                     "age": 42
                   },
                   "__id": 1
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
                         "__id": 2
                       }
                     },
                     "age": 42
                   },
                   "__id": 1
                 }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithListFields() {
        String json = """
                {
                   "targetClass": "inc.evil.serde.JsonSerdeTest$ListOfStrings",
                   "state": {
                     "values": {
                       "type": "java.util.ImmutableCollections$List12",
                       "value": {
                         "targetClass": "java.util.ImmutableCollections$List12",
                         "state": {
                           "e0": {"type": "java.lang.String", "value": "Funky"},
                           "e1": {"type": "java.lang.String", "value": "shit"}
                         },
                         "__id": 2
                       }
                     }
                   },
                   "__id": 1
                 }""";

        ListOfStrings actualDummy = jsonSerde.deserialize(json, ListOfStrings.class);

        ListOfStrings expectedDummy = new ListOfStrings(List.of("Funky", "shit"));
        assertEquals(expectedDummy, actualDummy);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithStringFields() {
        String json = """
                {
                  "targetClass" : "inc.evil.serde.JsonSerdeTest$StringFields",
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
                  "__id": 1
                }""";

        StringFields actualDeserializedInstance = jsonSerde.deserialize(json, StringFields.class);

        assertEquals(new StringFields("Mike", "Smith"), actualDeserializedInstance);
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
                  "__id": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectsWithSuperclasses() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$InheritedFields",
                  "state": {
                    "age": 29,
                    "name": {"type": "java.lang.String", "value": "Mike"}
                  },
                  "__id": 1
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
                  "__id": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithObjectFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$UpcastToObject",
                  "state": {
                    "source": {"type": "java.lang.Class", "value": "java.lang.String"},
                    "name": {"type": "java.lang.String", "value": "John"}
                  },
                  "__id": 1
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
                  "__id": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithDateFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$Dates",
                  "state": {
                    "localDate": {"type": "java.time.LocalDate", "value": "2020-12-25"},
                    "localDateTime": {"type": "java.time.LocalDateTime", "value": "2020-11-21T17:25:59"},
                    "offsetDateTime": {"type": "java.time.OffsetDateTime", "value": "2021-05-02T14:40:50.000000003Z"},
                    "zonedDateTime": {"type": "java.time.ZonedDateTime", "value": "2020-11-23T13:14:45+01:00[Europe/Paris]"}
                  },
                  "__id": 1
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
                  "__id": 1
                }""";

        NullWrappers actualInstance = jsonSerde.deserialize(json, NullWrappers.class);

        NullWrappers expectedInstance = new NullWrappers(null, 11123456789L);
        assertEquals(expectedInstance, actualInstance);
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
                  "__id": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithNullDatesFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$NullDates",
                  "state": {
                    "localDate": {"type": "java.time.LocalDate", "value": null},
                    "localDateTime": {"type": "java.time.LocalDateTime", "value": null},
                    "offsetDateTime": {"type": "java.time.OffsetDateTime", "value": null}
                  },
                  "__id": 1
                }""";

        NullDates actualInstance = jsonSerde.deserialize(json, NullDates.class);

        NullDates expectedInstance = new NullDates(null, null, null);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithBigNumbersFields() {
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
                  "__id": 1
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
                  "__id": 1
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
                  "__id": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithPeriodsAndDurationFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$PeriodsAndDurations",
                  "state": {
                    "period": {"type": "java.time.Period", "value": "P1D"},
                    "duration": {"type": "java.time.Duration", "value": "PT2M"}
                  },
                  "__id": 1
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
                  "__id": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializFromJson_objectWithNullArraysFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$NullArrays",
                  "state": {
                    "bytes": null,
                    "strings": null
                  },
                  "__id": 1
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
                      "strings": {"type": "java.util.List", "value": null}
                    },
                    "__id": 1
                  }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithNullInterfaceFields() {
        String json = """
                {
                    "targetClass": "inc.evil.serde.JsonSerdeTest$NullInterfaceFields",
                    "state": {
                      "strings": {"type": "java.util.List", "value": null}
                    },
                    "__id": 1
                  }""";

        NullInterfaceFields actualInstance = jsonSerde.deserialize(json, NullInterfaceFields.class);

        NullInterfaceFields expectedInstance = new NullInterfaceFields(null);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithNullFieldsOfTypeClass() {
        NullClassFields nullClassFields = new NullClassFields(null, "Mike");

        String actualJson = jsonSerde.serialize(nullClassFields);

        String expectedJson = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$NullClassFields",
                  "state": {
                    "targetClass": {"type": "java.lang.Class", "value": null},
                    "name": {"type": "java.lang.String", "value": "Mike"}
                  },
                  "__id": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithNullFieldsOfTypeClass() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$NullClassFields",
                  "state": {
                    "targetClass": {"type": "java.lang.Class", "value": null},
                    "name": {"type": "java.lang.String", "value": "Mike"}
                  },
                  "__id": 1
                }""";

        NullClassFields actualInstance = jsonSerde.deserialize(json, NullClassFields.class);

        NullClassFields expectedInstance = new NullClassFields(null, "Mike");
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectsWithObjectArraysFields() {
        ObjectArrays objectArrays = new ObjectArrays(
                new Object[]{
                        Override.class, "Mike", 42, new StringFields("John", "Doe")
                }
        );

        String actualJson = jsonSerde.serialize(objectArrays);

        String expectedJson = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$ObjectArrays",
                  "state": {
                    "objects": {
                      "type": "[Ljava.lang.Object;",
                      "value": [
                        {"type": "java.lang.Class", "value": "java.lang.Override"},
                        {"type": "java.lang.String", "value": "Mike"},
                        {"type": "java.lang.Integer", "value": 42},
                        {
                          "type": "inc.evil.serde.JsonSerdeTest$StringFields",
                          "value": {
                            "targetClass": "inc.evil.serde.JsonSerdeTest$StringFields",
                            "state": {
                              "firstName": {"type": "java.lang.String", "value": "John"},
                              "lastName": {"type": "java.lang.String", "value": "Doe"}
                            },
                            "__id": 2
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
    public void shouldBeAbleToDeserializeFromJson_objectsWithObjectArraysFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$ObjectArrays",
                  "state": {
                    "objects": {
                      "type": "[Ljava.lang.Object;",
                      "value": [
                        {"type": "java.lang.Class", "value": "java.lang.Override"},
                        {"type": "java.lang.String", "value": "Mike"},
                        {"type": "java.lang.Integer", "value": 42},
                        {
                          "type": "inc.evil.serde.JsonSerdeTest$StringFields",
                          "value": {
                            "targetClass": "inc.evil.serde.JsonSerdeTest$StringFields",
                            "state": {
                              "firstName": {"type": "java.lang.String", "value": "John"},
                              "lastName": {"type": "java.lang.String", "value": "Doe"}
                            },
                            "__id": 2
                          }
                        }
                      ]
                    }
                  },
                  "__id": 1
                }""";

        ObjectArrays actualInstance = jsonSerde.deserialize(json, ObjectArrays.class);

        ObjectArrays expectedInstance = new ObjectArrays(
                new Object[]{
                        Override.class, "Mike", 42, new StringFields("John", "Doe")
                }
        );
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectsWithClashingFieldNames() {
        ClashingFieldNames clashingFieldNames = new ClashingFieldNames("John", new StringFields("Mike", "Smith"));

        String actualJson = jsonSerde.serialize(clashingFieldNames);

        String expectedJson = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$ClashingFieldNames",
                  "state": {
                    "inc.evil.serde.JsonSerdeTest$ClashingFieldNames.name": {
                      "type": "inc.evil.serde.JsonSerdeTest$StringFields",
                      "value": {
                        "targetClass": "inc.evil.serde.JsonSerdeTest$StringFields",
                        "state": {
                          "firstName": {"type": "java.lang.String", "value": "Mike"},
                          "lastName": {"type": "java.lang.String", "value": "Smith"}},
                        "__id": 2
                      }
                    },
                    "inc.evil.serde.JsonSerdeTest$Named.name": {"type": "java.lang.String", "value": "John"}
                  },
                  "__id": 1
                }""";
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectsWithClashingFieldNames() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$ClashingFieldNames",
                  "state": {
                    "inc.evil.serde.JsonSerdeTest$ClashingFieldNames.name": {
                      "type": "inc.evil.serde.JsonSerdeTest$StringFields",
                      "value": {
                        "targetClass": "inc.evil.serde.JsonSerdeTest$StringFields",
                        "state": {
                          "firstName": {"type": "java.lang.String", "value": "Mike"},
                          "lastName": {"type": "java.lang.String", "value": "Smith"}},
                        "__id": 2
                      }
                    },
                    "inc.evil.serde.JsonSerdeTest$Named.name": {"type": "java.lang.String", "value": "John"}
                  },
                  "__id": 1
                }""";

        ClashingFieldNames actualInstance = jsonSerde.deserialize(json, ClashingFieldNames.class);

        ClashingFieldNames expectedInstance = new ClashingFieldNames("John", new StringFields("Mike", "Smith"));
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_genericObject() {
        Node<String, Integer> node = new Node<>(222, "Mike", 42, new Node<>(333, "Dennis", 45));

        String actualJson = jsonSerde.serialize(node);

        String expectedJson = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$Node",
                  "state": {"hash": 222, "key": {"type": "java.lang.String", "value": "Mike"},
                    "val": {"type": "java.lang.Integer", "value": 42},
                    "next": {
                      "type": "inc.evil.serde.JsonSerdeTest$Node",
                      "value": {
                        "targetClass": "inc.evil.serde.JsonSerdeTest$Node",
                        "state": {
                          "hash": 333,
                          "key": {"type": "java.lang.String", "value": "Dennis"},
                          "val": {"type": "java.lang.Integer", "value": 45},
                          "next": {"type": "inc.evil.serde.JsonSerdeTest$Node", "value": null}
                        },
                        "__id": 2
                      }
                    }
                  },
                  "__id": 1
                }
                """;
        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_genericObject() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.JsonSerdeTest$Node",
                  "state": {"hash": 222, "key": {"type": "java.lang.String", "value": "Mike"},
                    "val": {"type": "java.lang.Integer", "value": 42},
                    "next": {
                      "type": "inc.evil.serde.JsonSerdeTest$Node",
                      "value": {
                        "targetClass": "inc.evil.serde.JsonSerdeTest$Node",
                        "state": {
                          "hash": 333,
                          "key": {"type": "java.lang.String", "value": "Dennis"},
                          "val": {"type": "java.lang.Integer", "value": 45},
                          "next": {"type": "inc.evil.serde.JsonSerdeTest$Node", "value": null}
                        },
                        "__id": 2
                      }
                    }
                  },
                  "__id": 1
                }
                """;

        Node<String, Integer> actualInstance = jsonSerde.deserialize(json, Node.class);

        Node<String, Integer> expectedInstance = new Node<>(222, "Mike", 42, new Node<>(333, "Dennis", 45));
        assertEquals(expectedInstance, actualInstance);
    }

    public static class Node<K, V> {
        final int hash;
        final K key;
        volatile V val;
        volatile Node<K, V> next;

        Node(int hash, K key, V val) {
            this.hash = hash;
            this.key = key;
            this.val = val;
        }

        Node(int hash, K key, V val, Node<K, V> next) {
            this(hash, key, val);
            this.next = next;
        }

        public final int hashCode() {
            return key.hashCode() ^ val.hashCode();
        }

        public final boolean equals(Object o) {
            Object k, v, u;
            Node<?, ?> e;
            return ((o instanceof Node) &&
                    (k = (e = (Node<?, ?>) o).key) != null &&
                    (v = e.val) != null &&
                    (k == key || k.equals(key)) &&
                    (v == (u = val) || v.equals(u)));
        }

        @Override
        public String toString() {
            return "Node{" +
                    "hash=" + hash +
                    ", key=" + key +
                    ", val=" + val +
                    ", next=" + next +
                    '}';
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class NullClassFields {
        private final Class<?> targetClass;
        private final String name;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class ObjectArrays {
        private final Object[] objects;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class NullInterfaceFields {
        private List<String> strings;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class NullArrays {
        private final byte[] bytes;
        private final StringFields[] strings;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class PeriodsAndDurations {
        private final Period period;
        private final Duration duration;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class BigNumbers {
        private final BigDecimal bigDecimal;
        private final BigInteger bigInteger;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class NullWrappers {
        private final Long id;
        private final long age;
    }

    public static class ClashingFieldNames extends Named {
        private final StringFields name;

        public ClashingFieldNames(String name, StringFields name1) {
            super(name);
            this.name = name1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClashingFieldNames that = (ClashingFieldNames) o;

            return name != null ? name.equals(that.name) : that.name == null;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class NullDates {
        private final LocalDate localDate;
        private final LocalDateTime localDateTime;
        private final OffsetDateTime offsetDateTime;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class Dates {
        private final LocalDate localDate;
        private final LocalDateTime localDateTime;
        private final OffsetDateTime offsetDateTime;
        private final ZonedDateTime zonedDateTime;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class UpcastToObject {
        private final Object source;
        private final String name;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class ClassLiteral {
        private Class<?> targetClass;
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

    public static class CircularInstance2 {
        private final CircularInstance1 circularInstance1;
        private final String name;
        public CircularInstance2(CircularInstance1 circularInstance1, String name) {
            this.circularInstance1 = circularInstance1;
            this.name = name;
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

        @Override
        public String toString() {
            return "InheritedFields{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class MultidimensionalArrays {

        private boolean[][] booleans;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class MapFields {
        private final Map<String, StringFields> map;
    }

    public static class NoFields {
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class FinalFields {
        private final String firstName;
        private final String lastName;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class StringFields {
        private String firstName;
        private String lastName;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class PrimitiveArrays {
        boolean[] booleanArray;
        byte[] byteArray;
        char[] charArray;
        short[] shortArray;
        int[] intArray;
        long[] longArray;
        float[] floatArray;
        double[] doubleArray;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class PojoArrays {
        private StringFields[] strings;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class PojoFields {
        private StringFields user;
        private int age;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class ListOfStrings {
        private List<String> values;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class EnumFieldValues {
        enum Season {SPRING, WINTER}

        private Season season;
        private String name;
        private Season nullableSeason;
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

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class PrimitivesTypes {
        private boolean booleanField;
        private byte byteField;
        private char charField;
        private short shortField;
        private int intField;
        private long longField;
        private float floatField;
        private double doubleField;
    }
}
