package io.github.romangraef.jdacommandinterface.core.converters

import io.github.romangraef.jdacommandinterface.core.Context
import io.github.romangraef.jdacommandinterface.core.ConversionException
import java.util.regex.Pattern

object IDConverter : Converter<Long> {
    @Throws(ConversionException::class)
    override fun convert(context: Context, arg: String): Long {
        val matcher = ID_PATTERN.matcher(arg)
        if (!matcher.find()) throw ConversionException()
        return matcher.group().toLong()
    }

    private val ID_PATTERN = Pattern.compile("[0-9]+")!!
}