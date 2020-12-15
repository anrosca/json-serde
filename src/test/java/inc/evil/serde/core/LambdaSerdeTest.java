package inc.evil.serde.core;

import inc.evil.serde.JsonMapper;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    public void shouldBeAbleToDeserializeFromJson_objectWithSerializableLambdaFields() {
        String json = """
                {
                  "targetClass": "inc.evil.serde.core.LambdaSerdeTest$Lambdas",
                  "__id": 1,
                  "state": {
                    "age": 42,
                    "intConsumer": {
                      "type": "inc.evil.serde.core.LambdaSerdeTest$$Lambda$299/0x0000000800c10040",
                      "value": {
                        "type": "java.lang.invoke.SerializedLambda",
                        "value": {
                          "targetClass": "java.lang.invoke.SerializedLambda",
                          "__id": 2,
                          "state": {
                            "capturingClass": {
                              "type": "java.lang.Class",
                              "value": "inc.evil.serde.core.LambdaSerdeTest"
                            },
                            "functionalInterfaceClass": {
                              "type": "java.lang.String",
                              "value": "inc/evil/serde/core/LambdaSerdeTest$IntConsumer"
                            },
                            "functionalInterfaceMethodName": {
                              "type": "java.lang.String",
                              "value": "accept"
                            },
                            "functionalInterfaceMethodSignature": {
                              "type": "java.lang.String",
                              "value": "(I)V"
                            },
                            "implClass": {
                              "type": "java.lang.String",
                              "value": "inc/evil/serde/core/LambdaSerdeTest"
                            },
                            "implMethodName": {
                              "type": "java.lang.String",
                              "value": "lambda$shouldBeAbleToSerializeToJson_objectWithSerializableLambdaFields$6183a01$1"
                            },
                            "implMethodSignature": {
                              "type": "java.lang.String",
                              "value": "(I)V"
                            },
                            "implMethodKind": 6,
                            "instantiatedMethodType": {
                              "type": "java.lang.String",
                              "value": "(I)V"
                            },
                            "capturedArgs": {
                              "type": "[Ljava.lang.Object;",
                              "value": []
                            }
                          }
                        }
                      }
                    }
                  }
                }""";

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
