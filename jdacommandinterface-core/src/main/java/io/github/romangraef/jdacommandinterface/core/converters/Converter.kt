package io.github.romangraef.jdacommandinterface.core.converters;

import io.github.romangraef.jdacommandinterface.core.Context;
import io.github.romangraef.jdacommandinterface.core.ConversionException;
import net.dv8tion.jda.api.entities.Message;

@FunctionalInterface
public interface Converter<T> {
    default T convert(Context context, Message mes) throws ConversionException {
        return convert(context, mes.getContentRaw());
    }

    T convert(Context context, String arg) throws ConversionException;
}
