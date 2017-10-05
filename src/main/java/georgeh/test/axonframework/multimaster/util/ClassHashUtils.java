package georgeh.test.axonframework.multimaster.util;

import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;

public final class ClassHashUtils {

    /**
     * Computes sha1 of the class.
     */
    public static String getHash(Class<?> c) {
        try {
            return DigestUtils.sha1Hex(c.getClassLoader().getResourceAsStream(c.getName().replace('.', '/')+".class"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
}
