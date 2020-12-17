package inc.evil.serde;

import inc.evil.serde.extension.JsonFile;
import inc.evil.serde.extension.JsonFileParameterSupplier;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static inc.evil.serde.cast.TestUtils.assertJsonEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(JsonFileParameterSupplier.class)
public class JsonSerdeTest {

    private final JsonSerde jsonSerde = new SerdeFactory().defaultSerde();

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithClassFields(@JsonFile("/payloads/class-literals.json") String expectedJson) {
        ClassLiteral literal = new ClassLiteral(ArrayList.class);

        String actualJson = jsonSerde.serialize(literal);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithClassFields(@JsonFile("/payloads/class-literals.json") String json) {
        ClassLiteral actualInstance = jsonSerde.deserialize(json, ClassLiteral.class);

        ClassLiteral expectedInstance = new ClassLiteral(ArrayList.class);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleFromDeserialize_objectWithFinalFields(@JsonFile("/payloads/final-dto-field.json") String json) {
        FinalFields actualInstance = jsonSerde.deserialize(json, FinalFields.class);

        assertEquals(new FinalFields("Mike", "Smith"), actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithMultidimensionalArraysFields(
            @JsonFile("/payloads/multidimensional-primitive-array.json") String expectedJson) {
        MultidimensionalArrays arrays = new MultidimensionalArrays(
                new boolean[][]{
                        new boolean[]{false, true},
                        new boolean[]{true, false, true},
                        new boolean[]{true, true},
                        new boolean[]{true, false, false, true},
                }
        );

        String actualJson = jsonSerde.serialize(arrays);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithMultidimensionalArraysFields(
            @JsonFile("/payloads/multidimensional-primitive-array.json") String json) {
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
    public void shouldBeAbleToSerialize_objectWithNoFields(@JsonFile("/payloads/no-fields.json") String expectedJson) {
        NoFields noFields = new NoFields();

        String actualJson = jsonSerde.serialize(noFields);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserialize_objectWithNoFields(@JsonFile("/payloads/no-fields.json") String json) {
        NoFields actualInstance = jsonSerde.deserialize(json, NoFields.class);

        assertNotNull(actualInstance);
        assertEquals(NoFields.class, actualInstance.getClass());
    }

    @Test
    public void shouldBeAbleToSerialize_objectWithPrimitiveArraysFields(@JsonFile("/payloads/primitive-arrays.json") String expectedJson) {
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

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserialize_objectWithPrimitiveArraysFields(@JsonFile("/payloads/primitive-arrays.json") String json) {
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
    public void shouldBeAbleToSerializeToJson_objectWithStringFields(@JsonFile("/payloads/dto-string-fields.json") String expectedJson) {
        StringFields dummy = new StringFields("Mike", "Smith");

        String actualJson = jsonSerde.serialize(dummy);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithArrayFields(@JsonFile("/payloads/dto-array-fields.json") String expectedJson) {
        PojoArrays dummy = new PojoArrays(new StringFields[]{new StringFields("Mike", "Smith"), new StringFields("Dennis", "Ritchie")});

        String actualJson = jsonSerde.serialize(dummy);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithArrayOfObjectsFields(@JsonFile("/payloads/dto-array-fields.json") String json) {
        PojoArrays actualObject = jsonSerde.deserialize(json, PojoArrays.class);

        PojoArrays expectedObject = new PojoArrays(new StringFields[]{new StringFields("Mike", "Smith"), new StringFields("Dennis", "Ritchie")});
        assertEquals(expectedObject, actualObject);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithPrimitiveFields(@JsonFile("/payloads/primitive-fields.json") String expectedJson) {
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

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithPrimitiveFields(@JsonFile("/payloads/primitive-fields.json") String json) {
        PrimitivesTypes actualObject = jsonSerde.deserialize(json, PrimitivesTypes.class);

        PrimitivesTypes expectedObject = new PrimitivesTypes(true, (byte) 1, (char) 2, (short) 3, 4, 5L, 6.5f, 7.5D);
        assertEquals(expectedObject, actualObject);
    }

    @Test
    public void shouldIgnoreStaticAndTransientFields_uponSerialization(@JsonFile("/payloads/ignore-static-fields.json") String expectedJson) {
        IgnoreStaticFields dummy = new IgnoreStaticFields("Mike");

        String actualJson = jsonSerde.serialize(dummy);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldIgnoreStaticAndTransientFields_uponDeserialization(@JsonFile("/payloads/ignore-static-fields.json") String json) {
        IgnoreStaticFields actualObject = jsonSerde.deserialize(json, IgnoreStaticFields.class);

        IgnoreStaticFields expectedObject = new IgnoreStaticFields("Mike");
        assertEquals(expectedObject, actualObject);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithNestedObjects(@JsonFile("/payloads/nested-dto.json") String expectedJson) {
        PojoFields dummy = new PojoFields(new StringFields("Mike", "Smith"), 42);

        String actualJson = jsonSerde.serialize(dummy);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithNestedObjects(@JsonFile("/payloads/nested-dto.json") String json) {
        PojoFields actualObject = jsonSerde.deserialize(json, PojoFields.class);

        PojoFields expectedObject = new PojoFields(new StringFields("Mike", "Smith"), 42);
        assertEquals(expectedObject, actualObject);
    }

    @Test
    public void shouldBeAbleToSerializeFromJson_objectWithEnumFields(@JsonFile("/payloads/enum-fields.json") String expectedJson) {
        EnumFieldValues dummy = new EnumFieldValues(EnumFieldValues.Season.SPRING, "Mike", null);

        String actualJson = jsonSerde.serialize(dummy);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithEnumFields(@JsonFile("/payloads/enum-fields.json") String json) {
        EnumFieldValues actualDeserializedInstance = jsonSerde.deserialize(json, EnumFieldValues.class);

        EnumFieldValues expectedInstance = new EnumFieldValues(EnumFieldValues.Season.SPRING, "Mike", null);
        assertEquals(actualDeserializedInstance, expectedInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithListFields(@JsonFile("/payloads/list-of-strings.json") String expectedJson) {
        ListOfStrings dummy = new ListOfStrings(Arrays.asList("Funky", "shit"));

        String actualJson = jsonSerde.serialize(dummy);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithCircularDependencies(@JsonFile("/payloads/circular-dependencies.json") String json) {

        CircularInstance1 actualInstance = jsonSerde.deserialize(json, CircularInstance1.class);

        CircularInstance1 expectedInstance = new CircularInstance1();
        CircularInstance2 instance2 = new CircularInstance2(expectedInstance, "Mike");
        expectedInstance.age = 42;
        expectedInstance.circularInstance2 = instance2;

        assertEquals(actualInstance.age, expectedInstance.age);
        assertEquals(actualInstance.circularInstance2.name, expectedInstance.circularInstance2.name);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithCircularDependencies(@JsonFile("/payloads/circular-dependencies.json") String expectedJson) {
        CircularInstance1 instance1 = new CircularInstance1();
        CircularInstance2 instance2 = new CircularInstance2(instance1, "Mike");
        instance1.age = 42;
        instance1.circularInstance2 = instance2;

        String actualJson = jsonSerde.serialize(instance1);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithListFields(@JsonFile("/payloads/list-of-strings.json") String json) {
        ListOfStrings actualDummy = jsonSerde.deserialize(json, ListOfStrings.class);

        ListOfStrings expectedDummy = new ListOfStrings(Arrays.asList("Funky", "shit"));
        assertEquals(expectedDummy, actualDummy);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithStringFields(@JsonFile("/payloads/dto-string-fields.json") String json) {
        StringFields actualDeserializedInstance = jsonSerde.deserialize(json, StringFields.class);

        assertEquals(new StringFields("Mike", "Smith"), actualDeserializedInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectsWithSuperclasses(@JsonFile("/payloads/inherited-fields.json") String expectedJson) {
        InheritedFields inheritedFields = new InheritedFields("Mike", 29);

        String actualJson = jsonSerde.serialize(inheritedFields);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectsWithSuperclasses(@JsonFile("/payloads/inherited-fields.json") String json) {
        InheritedFields actualInstance = jsonSerde.deserialize(json, InheritedFields.class);

        InheritedFields expectedInstance = new InheritedFields("Mike", 29);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithObjectFields(@JsonFile("/payloads/object-fields.json") String expectedJson) {
        UpcastToObject upcastToObject = new UpcastToObject(String.class, "John");

        String actualJson = jsonSerde.serialize(upcastToObject);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithObjectFields(@JsonFile("/payloads/object-fields.json") String json) {
        UpcastToObject actualInstance = jsonSerde.deserialize(json, UpcastToObject.class);

        UpcastToObject expectedInstance = new UpcastToObject(String.class, "John");
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithDateFields(@JsonFile("/payloads/date-fields.json") String expectedJson) {
        Dates dates = new Dates(
                LocalDate.of(2020, Month.DECEMBER, 25),
                LocalDateTime.of(2020, Month.NOVEMBER, 21, 17, 25, 59),
                OffsetDateTime.of(2021, 5, 2, 14, 40, 50, 3, ZoneOffset.UTC),
                ZonedDateTime.of(LocalDateTime.of(2020, 11, 23, 13, 14, 45), ZoneId.of("Europe/Paris")));

        String actualJson = jsonSerde.serialize(dates);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithDateFields(@JsonFile("/payloads/date-fields.json") String json) {
        Dates actualInstance = jsonSerde.deserialize(json, Dates.class);

        Dates expectedInstance = new Dates(
                LocalDate.of(2020, Month.DECEMBER, 25),
                LocalDateTime.of(2020, Month.NOVEMBER, 21, 17, 25, 59),
                OffsetDateTime.of(2021, 5, 2, 14, 40, 50, 3, ZoneOffset.UTC),
                ZonedDateTime.of(LocalDateTime.of(2020, 11, 23, 13, 14, 45), ZoneId.of("Europe/Paris")));
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_nullPrimitiveWrappers(@JsonFile("/payloads/null-primitive-wrapper.json") String json) {
        NullWrappers actualInstance = jsonSerde.deserialize(json, NullWrappers.class);

        NullWrappers expectedInstance = new NullWrappers(null, 11123456789L);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_nullPrimitiveWrappers(@JsonFile("/payloads/null-primitive-wrapper.json") String expectedJson) {
        NullWrappers nullWrappers = new NullWrappers(null, 11123456789L);

        String actualJson = jsonSerde.serialize(nullWrappers);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithNullDatesFields(@JsonFile("/payloads/null-dates.json") String expectedJson) {
        NullDates nullDates = new NullDates(null, null, null);

        String actualJson = jsonSerde.serialize(nullDates);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithNullDatesFields(@JsonFile("/payloads/null-dates.json") String json) {
        NullDates actualInstance = jsonSerde.deserialize(json, NullDates.class);

        NullDates expectedInstance = new NullDates(null, null, null);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithBigNumbersFields(@JsonFile("/payloads/big-numbers.json") String json) {
        BigNumbers actualInstance = jsonSerde.deserialize(json, BigNumbers.class);

        BigNumbers expectedInstance = new BigNumbers(
                new BigDecimal("66612334556798765645435342334357676863343434354672234566.69"),
                new BigInteger("123445678990766")
        );
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithBigNumbersFields(@JsonFile("/payloads/big-numbers.json") String expectedJson) {
        BigNumbers bigNumbers = new BigNumbers(
                new BigDecimal("66612334556798765645435342334357676863343434354672234566.69"),
                new BigInteger("123445678990766")
        );

        String actualJson = jsonSerde.serialize(bigNumbers);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithPeriodsAndDurationFields(@JsonFile("/payloads/periods-durations.json") String expectedJson) {
        PeriodsAndDurations periodsAndDurations = new PeriodsAndDurations(
                Period.ofDays(1),
                Duration.ofMinutes(2)
        );

        String actualJson = jsonSerde.serialize(periodsAndDurations);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithPeriodsAndDurationFields(@JsonFile("/payloads/periods-durations.json") String json) {
        PeriodsAndDurations actualInstance = jsonSerde.deserialize(json, PeriodsAndDurations.class);

        PeriodsAndDurations expectedInstance = new PeriodsAndDurations(
                Period.ofDays(1),
                Duration.ofMinutes(2)
        );
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithNullArraysFields(@JsonFile("/payloads/null-arrays.json") String expectedJson) {
        NullArrays nullArrays = new NullArrays(null, null);

        String actualJson = jsonSerde.serialize(nullArrays);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithNullArraysFields(@JsonFile("/payloads/null-arrays.json") String json) {
        NullArrays actualInstance = jsonSerde.deserialize(json, NullArrays.class);

        NullArrays expectedInstance = new NullArrays(null, null);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithNullInterfaceFields(@JsonFile("/payloads/null-interface-fields.json") String expectedJson) {
        NullInterfaceFields nullInterfaceFields = new NullInterfaceFields(null);

        String actualJson = jsonSerde.serialize(nullInterfaceFields);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithNullInterfaceFields(@JsonFile("/payloads/null-interface-fields.json") String json) {
        NullInterfaceFields actualInstance = jsonSerde.deserialize(json, NullInterfaceFields.class);

        NullInterfaceFields expectedInstance = new NullInterfaceFields(null);
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithNullFieldsOfTypeClass(@JsonFile("/payloads/null-class-fields.json") String expectedJson) {
        NullClassFields nullClassFields = new NullClassFields(null, "Mike");

        String actualJson = jsonSerde.serialize(nullClassFields);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithNullFieldsOfTypeClass(@JsonFile("/payloads/null-class-fields.json") String json) {
        NullClassFields actualInstance = jsonSerde.deserialize(json, NullClassFields.class);

        NullClassFields expectedInstance = new NullClassFields(null, "Mike");
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectsWithObjectArraysFields(@JsonFile("/payloads/object-arrays.json") String expectedJson) {
        ObjectArrays objectArrays = new ObjectArrays(
                new Object[]{
                        Override.class, "Mike", 42, new StringFields("John", "Doe")
                }
        );

        String actualJson = jsonSerde.serialize(objectArrays);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectsWithObjectArraysFields(@JsonFile("/payloads/object-arrays.json") String json) {
        ObjectArrays actualInstance = jsonSerde.deserialize(json, ObjectArrays.class);

        ObjectArrays expectedInstance = new ObjectArrays(
                new Object[]{
                        Override.class, "Mike", 42, new StringFields("John", "Doe")
                }
        );
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_objectsWithClashingFieldNames(@JsonFile("/payloads/clashing-field-names.json") String expectedJson) {
        ClashingFieldNames clashingFieldNames = new ClashingFieldNames("John", new StringFields("Mike", "Smith"));

        String actualJson = jsonSerde.serialize(clashingFieldNames);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectsWithClashingFieldNames(@JsonFile("/payloads/clashing-field-names.json") String json) {
        ClashingFieldNames actualInstance = jsonSerde.deserialize(json, ClashingFieldNames.class);

        ClashingFieldNames expectedInstance = new ClashingFieldNames("John", new StringFields("Mike", "Smith"));
        assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeToJson_genericObject(@JsonFile("/payloads/generic-object.json") String expectedJson) {
        Node<String, Integer> node = new Node<>(222, "Mike", 42, new Node<>(333, "Dennis", 45));

        String actualJson = jsonSerde.serialize(node);

        assertJsonEquals(expectedJson, actualJson);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_genericObject(@JsonFile("/payloads/generic-object.json") String json) {
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
