package io.github.romangraef.jdacommandinterface.core

import java.lang.reflect.Method
import java.lang.reflect.Parameter

abstract class Command : ICommand {
    private val invoke: Method = javaClass.methods
            .filter { it.name == "execute" }
            .firstOrNull { Context::class.java.isAssignableFrom(it.parameterTypes[0]) }
            ?: throw RuntimeException("Please implement a method named `execute` in ${javaClass.simpleName}")

    final override val isVarArgs: Boolean = invoke.isVarArgs
    final override val argCount: Int = invoke.parameterCount - 1 - if (isVarArgs) 1 else 0
    final override val description: CommandDescription = javaClass.getAnnotation(CommandDescription::class.java)
    final override val checks: Array<Check> = javaClass.getAnnotation(Checks::class.java)?.value ?: arrayOf()
    override val parameters: Array<Parameter> = invoke.parameters

    override fun invokeCommand(arguments: Array<Any?>) {
        invoke.invoke(this, *arguments)
    }


    init {
        invoke.isAccessible = true
    }
}