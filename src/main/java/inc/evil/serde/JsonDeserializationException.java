package inc.evil.serde;

public class JsonDeserializationException extends RuntimeException {
    public JsonDeserializationException(Exception cause) {
        super(cause);
    }
}
