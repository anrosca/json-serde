package inc.evil.serde;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Constructor;

public class ObjectFactory {

    public Object makeInstance(Class<?> clazz) {
        try {
            return doMakeInstance(clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object doMakeInstance(Class<?> clazz) throws Exception {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                constructor.setAccessible(true);
                return constructor.newInstance();
            }
        }
        Objenesis objenesis = new ObjenesisStd();
        return objenesis.newInstance(clazz);
    }
}
