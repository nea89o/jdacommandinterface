package io.github.romangraef.jdacommandinterface.core.converters

import io.github.romangraef.jdacommandinterface.core.Context
import io.github.romangraef.jdacommandinterface.core.ConversionException
import java.awt.Color

object ColorConverter : Converter<Color> {
    @Throws(ConversionException::class)
    override fun convert(context: Context, arg: String): Color {
        if (arg.length == 7 && arg.startsWith("#")) {
            return convertHex(arg.substring(1))
        }
        if (arg.length == 8 && arg.startsWith("0x")) {
            return convertHex(arg.substring(2))
        }
        if (arg.length == 6) {
            return convertHex(arg)
        }
        throw ConversionException("Invalid color code. Use a hex code like `#ffafe0`")
    }

    private fun convertHex(hex: String): Color {
        return Color(hex.toInt(16))
    }

}