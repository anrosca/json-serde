package inc.evil.serde;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnnotationValuesSerdeTest {
    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void shouldBeAbleToSerializeAndDeserialize_objectsWithAnnotationValues() {
        AnnotationHolder expectedHolder = new AnnotationHolder(AnnotationHolder.class.getAnnotation(Value.class));

        String json = jsonMapper.serialize(expectedHolder);
        AnnotationHolder actualHolder = jsonMapper.deserialize(json, AnnotationHolder.class);

        assertEquals(expectedHolder.value.value(), actualHolder.value.value());
    }

    @RequiredArgsConstructor
    @ToString
    @Value("welcome!!!")
    public static class AnnotationHolder {
        private final Value value;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Value {
        String value();
    }
}
