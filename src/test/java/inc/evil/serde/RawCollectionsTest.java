package inc.evil.serde;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RawCollectionsTest {
    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void shouldBeAbleToSerializeAndDeserialize_rawCollections() {
        List list = new ArrayList(Arrays.asList("one", 42));
        RawListHolder listHolder = new RawListHolder(list);

        String json = jsonMapper.serialize(listHolder);
        RawListHolder actualInstance = jsonMapper.deserialize(json, RawListHolder.class);

        assertEquals(actualInstance, listHolder);
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class RawListHolder {
        private final List list;
    }
}
