package io.github.romangraef.jdacommandinterface.core

import io.github.romangraef.jdacommandinterface.core.util.ArgumentUtil
import java.lang.reflect.Parameter

interface ICommand {

    val checks: Array<Check>
    val argCount: Int
    val description: CommandDescription
    val parameters: Array<Parameter>
    val isVarArgs: Boolean

    @Throws(Exception::class)
    fun invokeCommand(arguments: Array<Any?>)

    /**
     * @return a list of failed checks. if len > 0 this indicates an error
     */
    fun runChecks(context: Context): List<Check> {
        return checks.filter { !it.check(context) }
    }

    val usage: Array<String>
        get() = description.usage
    val name: String
        get() = description.name

    val shortDescription: String?
        get() = if (description.description.isBlank()) {
            null
        } else {
            description.description
        }

    val longDescription: String?
        get() = if (description.longDescription.isBlank()) {
            shortDescription
        } else {
            description.longDescription
        }

    val triggers: Array<String>
        get() = description.triggers + arrayOf(name)

    val isHidden: Boolean
        get() = description.hidden

    fun isTrigger(text: String): Boolean {
        return triggers.any { text.equals(it, ignoreCase = true) }
    }


    @Throws(NoConverterFoundException::class, ConversionException::class, NotEnoughArgumentsException::class)
    fun runCommand(context: Context, args: Array<String>) {
        val finalArgs = ArgumentUtil.getArguments(context, args, parameters)
        try {
            invokeCommand(finalArgs)
        } catch (e: Exception) {
            context.captureError(e)
        }
    }


}