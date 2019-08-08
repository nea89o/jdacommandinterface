package io.github.romangraef.jdacommandinterface.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Checks {
    Check[] value() default {};
}
