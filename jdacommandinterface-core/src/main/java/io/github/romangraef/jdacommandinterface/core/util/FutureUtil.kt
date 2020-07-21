package io.github.romangraef.jdacommandinterface.core.util

import java.util.concurrent.CompletableFuture

object FutureUtil {
    @kotlin.jvm.JvmStatic
    fun <T> reject(t: Throwable?): CompletableFuture<T> {
        val fut = CompletableFuture<T>()
        fut.completeExceptionally(t)
        return fut
    }

    fun <T> resolve(result: T): CompletableFuture<T> {
        val fut = CompletableFuture<T>()
        fut.complete(result)
        return fut
    }
}