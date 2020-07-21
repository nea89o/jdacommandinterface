package io.github.romangraef.jdacommandinterface.core.converters

import io.github.romangraef.jdacommandinterface.core.Context
import io.github.romangraef.jdacommandinterface.core.ConversionException
import net.dv8tion.jda.api.entities.Message

@FunctionalInterface
interface Converter<T> {
    @Throws(ConversionException::class)
    fun convert(context: Context, mes: Message): T {
        return convert(context, mes.contentRaw)
    }

    @Throws(ConversionException::class)
    fun convert(context: Context, arg: String): T

    companion object {
        fun <T> create(internalConverter: (ctx: Context, arg: String) -> T): Converter<T> = object : Converter<T> {
            override fun convert(context: Context, arg: String): T = internalConverter.invoke(context, arg)
        }
    }
}