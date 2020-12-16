package inc.evil.serde;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NestedListsTest {
    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void shouldBeAbleToSerializeAndDeserialize_nestedLists() {
        List<List<User>> userList = new ArrayList<>();
        userList.add(new ArrayList<>(Arrays.asList(new User("Mike", "Smith"), new User("Dennis", "Ritchie"))));
        ListHolder listHolder = new ListHolder(userList);

        String json = jsonMapper.serialize(listHolder);
        ListHolder actualInstance = jsonMapper.deserialize(json, ListHolder.class);

        assertEquals(listHolder, actualInstance);
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class ListHolder {
        private final List<List<User>> listHolder;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class User {
        private final String firstName;
        private final String lastName;
    }
}
