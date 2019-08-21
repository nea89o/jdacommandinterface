package io.github.romangraef.jdacommandinterface.core.errors;

import io.github.romangraef.jdacommandinterface.core.Context;

/**
 * Base class for an ErrorHandler to be registered in a {@link CommandErrors}. Annotate your ErrorHandler with {@link
 * RegisterErrorHandler}
 *
 * @param <T> Type of the throwable that will be caught
 */
public abstract class ErrorHandler<T extends Throwable> {
    /**
     * This method will be called once the exception is caught.
     *
     * @param throwable the caught exception will be of type {@code T}
     * @param ctx the context under which the exception was thrown.
     */
    public abstract void handle(Throwable throwable, Context ctx);
}
