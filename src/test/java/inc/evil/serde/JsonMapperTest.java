package inc.evil.serde;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class JsonMapperTest {

    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void shouldBeAbleToSerializeAndDeserializeToJson() {
        Map<String, User> users = new ConcurrentHashMap<>();
        users.put("first", new User("Mike", "Smith"));
        users.put("second", new User("Dennie", "Ritchie"));

        String json = jsonMapper.serialize(users);
        Map<String, User> deserializedInstance = jsonMapper.deserialize(json, Map.class);

        assertEquals(users, deserializedInstance);
    }

    public static class User {
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
    }
}
