package inc.evil.serde;

public class JsonSerializationException extends RuntimeException {
    public JsonSerializationException(Exception cause) {
        super(cause);
    }
}
