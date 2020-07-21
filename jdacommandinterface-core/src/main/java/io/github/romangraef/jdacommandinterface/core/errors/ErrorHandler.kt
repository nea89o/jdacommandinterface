package io.github.romangraef.jdacommandinterface.core.errors

import io.github.romangraef.jdacommandinterface.core.Context

/**
 * Base class for an ErrorHandler to be registered in a [CommandErrors]. Annotate your ErrorHandler with [RegisterErrorHandler]
 *
 * @param <T> Type of the throwable that will be caught. Note: this checks root causes as well, so the actual exception your handler is called on might be of different type.
</T> */
abstract class ErrorHandler<T : Throwable> {
    /**
     * This method will be called once the exception is caught.
     *
     * @param throwable the caught exception will be of type `T`
     * @param ctx the context under which the exception was thrown.
     */
    abstract fun handle(throwable: Throwable, ctx: Context?)
}