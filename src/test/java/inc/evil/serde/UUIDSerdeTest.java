package inc.evil.serde;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UUIDSerdeTest {

    @Test
    public void shouldBeAbleToSerializeAndDeserializeAnUUID() {
        UUID expectedUUID = UUID.randomUUID();
        JsonMapper jsonMapper = new JsonMapper();

        String json = jsonMapper.serialize(expectedUUID);
        UUID actualUUID = jsonMapper.deserialize(json, UUID.class);

        assertEquals(expectedUUID, actualUUID);
    }
}
