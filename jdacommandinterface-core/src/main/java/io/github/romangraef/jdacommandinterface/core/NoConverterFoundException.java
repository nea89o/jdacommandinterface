package io.github.romangraef.jdacommandinterface.core;

public class NoConverterFoundException extends Exception {
    public <T> NoConverterFoundException(Class<T> clazz) {
        super("No converter found for class: " + clazz.getCanonicalName());
    }
}
