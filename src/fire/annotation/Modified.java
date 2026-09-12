package fire.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Indicates that a method is basically modified from the one in a supertype.
 * Thus, if the super one is changed, the method has to update to remain the same logic. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Modified{}
