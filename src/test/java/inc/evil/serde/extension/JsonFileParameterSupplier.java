package inc.evil.serde.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class JsonFileParameterSupplier implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == String.class &&
               parameterContext.isAnnotated(JsonFile.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.findAnnotation(JsonFile.class)
                .map(JsonFile::value)
                .map(this::readFile)
                .orElseThrow(() -> new RuntimeException("No file to read"));
    }

    private String readFile(String filePath) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            transferTo(getClass().getResourceAsStream(filePath), buffer);
            return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long transferTo(InputStream in, OutputStream out) throws IOException {
        Objects.requireNonNull(out, "out");
        long transferred = 0;
        int bufferSize = 4096;
        byte[] buffer = new byte[bufferSize];
        int read;
        while ((read = in.read(buffer, 0, bufferSize)) >= 0) {
            out.write(buffer, 0, read);
            transferred += read;
        }
        return transferred;
    }
}
