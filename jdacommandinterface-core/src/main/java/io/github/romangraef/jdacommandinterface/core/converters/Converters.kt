package io.github.romangraef.jdacommandinterface.core.converters

import io.github.romangraef.jdacommandinterface.core.Context
import io.github.romangraef.jdacommandinterface.core.ConversionException
import io.github.romangraef.jdacommandinterface.core.NoConverterFoundException
import io.github.romangraef.jdacommandinterface.core.util.RandomUtil
import net.dv8tion.jda.api.entities.ISnowflake
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import java.awt.Color

object Converters {
    private val converters: MutableMap<Class<*>, Converter<*>> = mutableMapOf()

    @Throws(ConversionException::class)
    private fun convertBoolean(arg: String): Boolean {
        if (arg.equals("yes", ignoreCase = true)) return true
        if (arg.equals("y", ignoreCase = true)) return true
        if (arg.equals("no", ignoreCase = true)) return false
        if (arg.equals("n", ignoreCase = true)) return false
        if (arg.equals("true", ignoreCase = true)) return true
        if (arg.equals("false", ignoreCase = true)) return false
        throw ConversionException()
    }

    @JvmStatic
    fun <T> registerConverter(clazz: Class<T>, converter: Converter<T>) {
        converters[clazz] = converter
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    @Throws(NoConverterFoundException::class)
    fun <T> findConverter(clazz: Class<out T>): Converter<T> {
        return converters[clazz] as? Converter<T> ?: throw NoConverterFoundException(clazz)
    }

    @JvmStatic
    @Throws(NoConverterFoundException::class, ConversionException::class)
    fun <T> convert(clazz: Class<out T>, context: Context, arg: String): T {
        return findConverter(clazz).convert(context, arg)
    }

    init {
        registerConverter(String::class.java, Converter.create { _, arg -> arg })
        registerConverter(TextChannel::class.java, TextChannelConverter)
        registerConverter(Boolean::class.java, Converter.create { _, arg -> convertBoolean(arg) })
        registerConverter(java.lang.Boolean.TYPE, Converter.create { _, arg -> convertBoolean(arg) })
        registerConverter(Long::class.java, Converter.create { _, arg -> arg.toLong() })
        registerConverter(java.lang.Long.TYPE, Converter.create { _, arg -> arg.toLong() })
        registerConverter(Int::class.java, Converter.create { _, arg -> Integer.valueOf(arg) })
        registerConverter(Integer.TYPE, Converter.create { _, arg -> arg.toInt() })
        registerConverter(Color::class.java, ColorConverter)
        registerConverter(User::class.java, UserConverter)
        registerConverter(ISnowflake::class.java, Converter.create { context, arg -> RandomUtil.createSnowflake(IDConverter.convert(context, arg)) })
        registerConverter(Role::class.java, RoleConverter)
    }
}