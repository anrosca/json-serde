package inc.evil.serde;

import inc.evil.serde.core.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SerdeFactory {
    public JsonSerde from(Set<SerdeFeature> features) {
        if (!features.contains(SerdeFeature.SERIALIZE_COMMON_COLLECTIONS_AS_ARRAYS)) {
            return defaultSerde();
        }
        List<SerializerDeserializer> serializerDeserializers = Arrays.asList(
                new NullSerde(),
                new PrimitiveTypeSerde(),
                new ArraySerde(Collections.emptyList()),
                new CommonDateSerde(),
                new ClassSerde(),
                new AtomicNumbersSerde(),
                new BigNumbersSerde(),
                new EnumSerde(),
                new StringSerde(),
                new NumericSerde(),
                new BooleanSerde(),
                new LambdaSerde(new ObjectSerde()),
                new ObjectSerde()
        );
        return new JsonSerde(serializerDeserializers);
    }

    public JsonSerde defaultSerde() {
        List<SerializerDeserializer> serializerDeserializers = Arrays.asList(
                new NullSerde(),
                new PrimitiveTypeSerde(),
                new ArraySerde(Arrays.asList(new CommonMapSerde(), new CommonCollectionSerde())),
                new CommonMapSerde(),
                new CommonCollectionSerde(),
                new CommonDateSerde(),
                new ClassSerde(),
                new AtomicNumbersSerde(),
                new BigNumbersSerde(),
                new EnumSerde(),
                new StringSerde(),
                new NumericSerde(),
                new BooleanSerde(),
                new LambdaSerde(new ObjectSerde()),
                new ObjectSerde()
        );
        return new JsonSerde(serializerDeserializers);
    }
}
