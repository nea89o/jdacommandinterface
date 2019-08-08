package io.github.romangraef.jdacommandinterface.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandDescription {
    String name();

    String[] triggers();

    String longDescription();

    String description();

    boolean hidden() default false;

    String[] usage();
}
