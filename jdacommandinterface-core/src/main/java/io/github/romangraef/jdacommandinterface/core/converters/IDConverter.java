package io.github.romangraef.jdacommandinterface.core.converters;

import io.github.romangraef.jdacommandinterface.core.Context;
import io.github.romangraef.jdacommandinterface.core.ConversionException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDConverter implements Converter<Long> {
    private static final Pattern ID_PATTERN = Pattern.compile("[0-9]+");
    public static IDConverter INSTANCE = new IDConverter();

    protected IDConverter() {
    }

    @Override
    public Long convert(Context ctx, String arg) throws ConversionException {
        Matcher matcher = ID_PATTERN.matcher(arg);
        if (!matcher.find())
            throw new ConversionException();
        return Long.valueOf(matcher.group());
    }
}
