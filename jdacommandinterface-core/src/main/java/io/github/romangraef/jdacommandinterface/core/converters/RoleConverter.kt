package io.github.romangraef.jdacommandinterface.core.converters

import io.github.romangraef.jdacommandinterface.core.Context
import io.github.romangraef.jdacommandinterface.core.ConversionException
import net.dv8tion.jda.api.entities.Role

object RoleConverter : Converter<Role> {
    @Throws(ConversionException::class)
    override fun convert(context: Context, arg: String): Role {
        try {
            val id: Long = IDConverter.convert(context, arg)
            val r = context.jda.getRoleById(id)
            if (r != null) {
                return r
            }
        } catch (e: ConversionException) {
        }
        return context.guild?.getRolesByName(arg, true)?.firstOrNull() ?: throw ConversionException()
    }
}