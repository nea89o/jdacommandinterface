package io.github.romangraef.jdacommandinterface.core.converters;

import io.github.romangraef.jdacommandinterface.core.Context;
import io.github.romangraef.jdacommandinterface.core.ConversionException;
import io.github.romangraef.jdacommandinterface.core.NoConverterFoundException;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import static io.github.romangraef.jdacommandinterface.core.util.RandomUtil.createSnowflake;

public class Converters {
    private static Map<Class, Converter> converters = new HashMap<>();


    static {
        registerConverter(String.class, (context, arg) -> arg);
        registerConverter(TextChannel.class, TextChannelConverter.INSTANCE);
        registerConverter(Boolean.class, (context, arg) -> convertBoolean(arg));
        registerConverter(Boolean.TYPE, (context, arg) -> convertBoolean(arg));
        registerConverter(Long.class, (context, arg) -> Long.parseLong(arg));
        registerConverter(Long.TYPE, (context, arg) -> Long.parseLong(arg));
        registerConverter(Integer.class, (context, arg) -> Integer.valueOf(arg));
        registerConverter(Integer.TYPE, (context, arg) -> Integer.parseInt(arg));
        registerConverter(Color.class, ColorConverter.INSTANCE);
        registerConverter(User.class, UserConverter.INSTANCE);
        registerConverter(ISnowflake.class, (context, arg) -> createSnowflake(IDConverter.INSTANCE.convert(context, arg)));
        registerConverter(Role.class, RoleConverter.INSTANCE);
    }


    private static Boolean convertBoolean(String arg) throws ConversionException {
        if (arg.equalsIgnoreCase("yes")) return true;
        if (arg.equalsIgnoreCase("y")) return true;
        if (arg.equalsIgnoreCase("no")) return false;
        if (arg.equalsIgnoreCase("n")) return false;
        if (arg.equalsIgnoreCase("true")) return true;
        if (arg.equalsIgnoreCase("false")) return false;
        throw new ConversionException();
    }


    public static <T> void registerConverter(Class<T> clazz, Converter<T> converter) {
        converters.put(clazz, converter);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Converter<T> findConverter(Class<T> clazz) throws NoConverterFoundException {
        Converter<T> converter = ((Converter<T>) converters.get(clazz));
        if (converter == null) {
            throw new NoConverterFoundException(clazz);
        }
        return converter;
    }


    public static <T> T convert(Class<T> clazz, Context context, String arg) throws NoConverterFoundException, ConversionException {
        return findConverter(clazz).convert(context, arg);
    }
}