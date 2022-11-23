package io.stargate.sdk.doc.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to map object with targete object.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Collection {
    
    /**
     * Provide a name for the collection.
     *
     * @return
     *      collection name.
     */
    public String value() default "";

}
