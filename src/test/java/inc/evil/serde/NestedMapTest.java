package inc.evil.serde;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NestedMapTest {
    enum Seasons { SPRING, SUMMER, AUTUMN, WINTER }

    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void shouldBeAbleToSerializeAndDeserializeMapWithEnumMapValues() {
        Map<Object, Map<Seasons, List<Seasons>>> map = new ConcurrentHashMap<>();
        Map<Seasons, List<Seasons>> userValue = new HashMap<>();
        userValue.put(Seasons.AUTUMN, Arrays.asList(Seasons.SUMMER, Seasons.WINTER));
        map.put(new User("Mike", "Smith"), userValue);
        MapHolder mapHolder = new MapHolder(map, null);

        String json = jsonMapper.serialize(mapHolder);
        MapHolder actualInstance = jsonMapper.deserialize(json, MapHolder.class);

        assertEquals(mapHolder, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeAndDeserializeNestedMaps() {
        Map<User, Map<String, String>> map = new HashMap<>();
        Map<String, String> userValue = new HashMap<>();
        userValue.put("John", "Dennis");
        map.put(new User("Mike", "Smith"), userValue);
        MapHolder mapHolder = new MapHolder(null, map);

        String json = jsonMapper.serialize(mapHolder);
        MapHolder actualInstance = jsonMapper.deserialize(json, MapHolder.class);

        assertEquals(mapHolder, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeAndDeserializeNestedMaps2() {
        Map<User, Map<String, Map<User, User>>> map = new HashMap<>();
        Map<String, Map<User, User>> userValue = new HashMap<>();
        Map<User, User> value = new HashMap<>(Collections.singletonMap(new User("Dennis", "Ritchie"), new User("Brian", "Kernighan")));
        userValue.put("John", value);
        map.put(new User("Mike", "Smith"), userValue);
        TripleMapHolder mapHolder = new TripleMapHolder(map);

        String json = jsonMapper.serialize(mapHolder);
        TripleMapHolder actualInstance = jsonMapper.deserialize(json, TripleMapHolder.class);

        assertEquals(mapHolder, actualInstance);
    }

    @Test
    public void shouldBeAbleToSerializeAndDeserialize_EnumMaps() {
        EnumMap<Seasons, List<Seasons>> seasons = new EnumMap<>(Seasons.class);
        seasons.put(Seasons.SPRING, new ArrayList<>(Arrays.asList(Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER)));
        EnumManHolder enumManHolder = new EnumManHolder(seasons);

        String json = jsonMapper.serialize(enumManHolder);
        EnumManHolder actualInstance = jsonMapper.deserialize(json, EnumManHolder.class);

        assertEquals(enumManHolder, actualInstance);
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class EnumManHolder {
        private final Map<Seasons, List<Seasons>> seasons;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class TripleMapHolder {
        private final Map<User, Map<String, Map<User, User>>> map;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class MapHolder {
        private final Map<Object, Map<Seasons, List<Seasons>>> map;
        private final Map<User, Map<String, String>> yetAnotherMap;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class User {
        private final String firstName;
        private final String lastName;
    }
}
