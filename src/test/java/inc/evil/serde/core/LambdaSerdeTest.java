package inc.evil.serde.core;

import inc.evil.serde.JsonMapper;
import inc.evil.serde.extension.JsonFile;
import inc.evil.serde.extension.JsonFileParameterSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(JsonFileParameterSupplier.class)
public class LambdaSerdeTest {
    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void shouldBeAbleToSerializeToJson_objectWithSerializableLambdaFields() {
        Lambdas lambdas = new Lambdas(42, (number) -> {});

        String actualJson = jsonMapper.serialize(lambdas);
        Lambdas actualInstance = jsonMapper.deserialize(actualJson, Lambdas.class);

        Lambdas expectedInstance = new Lambdas(42, (number) -> {});
        assertEquals(expectedInstance.age, actualInstance.age);
        assertNotNull(actualInstance.intConsumer);
    }

    @Test
    public void shouldBeAbleToDeserializeFromJson_objectWithSerializableLambdaFields(@JsonFile("/payloads/lambda.json") String json) {
        Lambdas actualInstance = jsonMapper.deserialize(json, Lambdas.class);

        Lambdas expectedInstance = new Lambdas(42, (number) -> {});
        assertEquals(expectedInstance.age, actualInstance.age);
        assertNotNull(actualInstance.intConsumer);
    }

    public static class Lambdas {
        private final int age;
        private final IntConsumer intConsumer;

        public Lambdas(int age, IntConsumer intConsumer) {
            this.age = age;
            this.intConsumer = intConsumer;
        }
    }

    interface IntConsumer extends Serializable {
        void accept(int number);
    }
}
