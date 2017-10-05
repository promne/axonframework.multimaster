package georgeh.test.axonframework.multimaster.query;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * By using this annotation on a event handler you link the tracking token to a specific class version
 * allowing to have multiple versions of the same handler/entity (due class being changed).  
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessingGroupTarget {
    Class<?> value();
}
