package io.github.romangraef.jdacommandinterface.core.errors;

import io.github.romangraef.jdacommandinterface.core.Context;

public abstract class ErrorHandler<T extends Throwable> {
    public abstract void handle(Throwable throwable, Context ctx);
}
