package georgeh.test.axonframework.multimaster.mongo;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentNameParts {

    Class<?> documentType();

    Class<?> handlerType();

}
