package org.ayfaar.ii.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
public @interface TableAdminType {
    Types value() default Types.FK;
    String keyField() default "";
    String nameField() default "name";
    int numberMin() default 0;
    int numberMax() default 100;
//    boolean manyToMany() default false;
}
