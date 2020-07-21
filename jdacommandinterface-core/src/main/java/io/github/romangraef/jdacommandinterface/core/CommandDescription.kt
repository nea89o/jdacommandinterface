package io.github.romangraef.jdacommandinterface.core

@Retention(AnnotationRetention.RUNTIME)
annotation class CommandDescription(
        val name: String,
        val triggers: Array<String> = [],
        val longDescription: String = "",
        val description: String = "",
        val hidden: Boolean = false,
        val usage: Array<String> = []
)