package org.ayfaar.app.annotations;

import org.ayfaar.app.services.moderation.Action;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD})
@Retention(RUNTIME)
public @interface Moderated {
    Action value();
    String command();
}
