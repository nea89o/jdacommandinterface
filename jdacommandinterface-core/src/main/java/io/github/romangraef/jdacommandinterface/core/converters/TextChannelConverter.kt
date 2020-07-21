package io.github.romangraef.jdacommandinterface.core.converters

import io.github.romangraef.jdacommandinterface.core.Context
import io.github.romangraef.jdacommandinterface.core.ConversionException
import net.dv8tion.jda.api.entities.TextChannel

object TextChannelConverter : Converter<TextChannel> {
    @Throws(ConversionException::class)
    override fun convert(context: Context, arg: String): TextChannel {
        try {
            val id: Long = IDConverter.convert(context, arg)
            val chan = context.jda.getTextChannelById(id)
            if (chan != null) {
                return chan
            }
        } catch (e: ConversionException) {
        }
        return context.guild?.getTextChannelsByName(arg, true)?.firstOrNull() ?: throw ConversionException()
    }
}