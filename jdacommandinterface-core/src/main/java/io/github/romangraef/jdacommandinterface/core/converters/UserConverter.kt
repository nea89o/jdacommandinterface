package io.github.romangraef.jdacommandinterface.core.converters

import io.github.romangraef.jdacommandinterface.core.Context
import io.github.romangraef.jdacommandinterface.core.ConversionException
import net.dv8tion.jda.api.entities.User

object UserConverter : Converter<User> {
    @Throws(ConversionException::class)
    override fun convert(context: Context, arg: String): User {
        try {
            val id: Long = IDConverter.convert(context, arg)
            val u = context.jda.getUserById(id)
            if (u != null) {
                return u
            }
        } catch (e: ConversionException) {
        }
        return context.jda.getUsersByName(arg, true).firstOrNull() ?: throw ConversionException()
    }
}