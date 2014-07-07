package org.ayfaar.app.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
public @interface ViewAsJoinedString {
    String modelField() default "";
    String joinField() default "";
    String joinBy() default ", ";
}
