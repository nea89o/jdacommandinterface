package io.github.romangraef.jdacommandinterface.core.util

import io.github.romangraef.jdacommandinterface.core.Context
import io.github.romangraef.jdacommandinterface.core.ConversionException
import io.github.romangraef.jdacommandinterface.core.NoConverterFoundException
import io.github.romangraef.jdacommandinterface.core.NotEnoughArgumentsException
import io.github.romangraef.jdacommandinterface.core.converters.Converters
import java.lang.reflect.Parameter

object ArgumentUtil {
    /**
     * For internal use. Converts a string list into a list of objects according to a method definition.
     *
     * @param context    the context of the command as passed to the [Converters]
     * @param args       the arguments to convert
     * @param parameters the parameters of the method
     * @return the arguments converted to objects.
     * @throws NoConverterFoundException   if the method asks for an object without a registered converter
     * @throws ConversionException         this is reraised from the converters and indicated some kind of user error
     * @throws NotEnoughArgumentsException if the provided arguments aren't enough to fulfill the needs of the method.
     */
    @JvmStatic
    @Throws(NoConverterFoundException::class, ConversionException::class, NotEnoughArgumentsException::class)
    fun getArguments(context: Context, args: Array<String>, parameters: Array<Parameter>): Array<Any?> {
        val finalArgs = arrayOfNulls<Any>(parameters.size)
        // First argument is the context
        finalArgs[0] = context

        // Convert rest arguments
        val normalArgs = getConvertedNormalArgs(args, parameters.copyOfRange(1, parameters.size), context)

        // Copy rest arguments into the final args array
        System.arraycopy(normalArgs, 0, finalArgs, 1, normalArgs.size)

        // Get Vararg
        val varArgParam = parameters
                .find { obj: Parameter -> obj.isVarArgs }

        if (varArgParam != null) {

            // Get Vararg type
            val clazz: Class<*> = varArgParam.type.componentType!!
            // Get String var args
            val varArgStrings: Array<String> = args.sliceArray(parameters.size - 1..args.size)

            // Convert those var args using Converters
            val varArgs: Array<Any?> = varArgStrings.map {
                Converters.convert(clazz, context, it)
            }.toTypedArray()

            // Save the vararg to the array
            finalArgs[parameters.size - 1] = varArgs
        }
        return finalArgs
    }

    @Throws(NoConverterFoundException::class, ConversionException::class, NotEnoughArgumentsException::class)
    private fun getConvertedNormalArgs(args: Array<String>,
                                       parameters: Array<Parameter>,
                                       context: Context): Array<Any?> = try {
        parameters
                .drop(1) // No Context
                .filter { !it.isVarArgs }
                .mapIndexed { index, p -> Converters.convert(p.type, context, args[index]) }
                .toTypedArray()
    } catch (e: ArrayIndexOutOfBoundsException) {
        throw NotEnoughArgumentsException(e)
    }

}