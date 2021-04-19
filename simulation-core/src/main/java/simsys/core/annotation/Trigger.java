package simsys.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All methods annotated with this annotation use triggers for other methods.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Trigger {
  // can trigger method more than one class (the main thing is that the methods have the same name)
  Class<?>[] clazz();
  String methodName();
  Class<?>[] args() default {};
}