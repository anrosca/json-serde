package inc.evil.serde;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonMapperTest {

    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void shouldBeAbleToSerializeAndDeserializeToJson_Maps() {
        assertThatMapDoesNotBreak(new ConcurrentHashMap<>());
        assertThatMapDoesNotBreak(new HashMap<>());
        assertThatMapDoesNotBreak(new TreeMap<>());
        assertThatMapDoesNotBreak(new LinkedHashMap<>());
        assertThatMapDoesNotBreak(new WeakHashMap<>());
        assertThatMapDoesNotBreak(Collections.synchronizedMap(new HashMap<>()));
        assertThatMapDoesNotBreak(Collections.synchronizedMap(new TreeMap<>()));
    }

    @Test
    public void shouldBeAbleToSerializeAndDeserializeToJson_setFromConcurrentMap() {
        assertThatSetDoesNotBreak(Collections.newSetFromMap(new ConcurrentHashMap<>()));
        assertThatSetDoesNotBreak(Collections.newSetFromMap(new HashMap<>()));
        assertThatSetDoesNotBreak(Collections.newSetFromMap(new LinkedHashMap<>()));
        assertThatSetDoesNotBreak(Collections.newSetFromMap(new TreeMap<>()));
        assertThatSetDoesNotBreak(new HashSet<>());
        assertThatSetDoesNotBreak(new TreeSet<>());
        assertThatSetDoesNotBreak(new LinkedHashSet<>());
        assertThatSetDoesNotBreak(Collections.synchronizedSet(new HashSet<>()));
        assertThatSetDoesNotBreak(Collections.synchronizedSet(new TreeSet<>()));
    }

    @SuppressWarnings("unchecked")
    private void assertThatMapDoesNotBreak(Map<User, List<User>> instanceToTest) {
        instanceToTest.put(new User("Mike", "Smith"), singletonList(new User("Robert", "Martin")));
        instanceToTest.put(new User("Dennie", "Ritchie"), singletonList(new User("Michael", "Feathers")));

        String json = jsonMapper.serialize(instanceToTest);
        Map<User, List<User>> deserializedInstance = jsonMapper.deserialize(json, Map.class);

        assertEquals(2, deserializedInstance.size());
        assertTrue(deserializedInstance.containsKey(new User("Mike", "Smith")));
        assertTrue(deserializedInstance.containsValue(singletonList(new User("Robert", "Martin"))));
        assertTrue(deserializedInstance.containsKey(new User("Dennie", "Ritchie")));
        assertTrue(deserializedInstance.containsValue(singletonList(new User("Michael", "Feathers"))));
        assertEquals(instanceToTest, deserializedInstance);
        assertEquals(instanceToTest.keySet(), deserializedInstance.keySet());
        assertEquals(instanceToTest.entrySet(), deserializedInstance.entrySet());
        assertEquals(instanceToTest.values().toString(), deserializedInstance.values().toString());
        deserializedInstance.put(new User("Martin", "Fowler"), emptyList());
        assertTrue(deserializedInstance.containsKey(new User("Martin", "Fowler")));
    }

    @SuppressWarnings("unchecked")
    private void assertThatSetDoesNotBreak(Set<User> instanceToTest) {
        instanceToTest.add(new User("Mike", "Smith"));
        instanceToTest.add(new User("Dennie", "Ritchie"));

        String json = jsonMapper.serialize(instanceToTest);
        Set<User> deserializedInstance = jsonMapper.deserialize(json, Set.class);

        assertEquals(2, deserializedInstance.size());
        assertTrue(deserializedInstance.contains(new User("Mike", "Smith")));
        assertTrue(deserializedInstance.contains(new User("Dennie", "Ritchie")));
        assertEquals(instanceToTest, deserializedInstance);
    }

    public static class User implements Comparable<User> {
        private final String firstName;
        private final String lastName;

        public User(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            User user = (User) o;

            if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
            return lastName != null ? lastName.equals(user.lastName) : user.lastName == null;
        }

        @Override
        public int hashCode() {
            int result = firstName != null ? firstName.hashCode() : 0;
            result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "User{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    '}';
        }

        @Override
        public int compareTo(User other) {
            int result = firstName.compareTo(other.firstName);
            return result != 0 ? result : lastName.compareTo(other.lastName);
        }
    }
}
