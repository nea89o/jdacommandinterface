package io.github.romangraef.jdacommandinterface.core.converters;


import io.github.romangraef.jdacommandinterface.core.Context;
import io.github.romangraef.jdacommandinterface.core.ConversionException;
import net.dv8tion.jda.api.entities.User;

public class UserConverter implements Converter<User> {
    public static final UserConverter INSTANCE = new UserConverter();

    protected UserConverter() {
    }

    @Override
    public User convert(Context context, String arg) throws ConversionException {
        try {
            long id = IDConverter.INSTANCE.convert(context, arg);
            User u = context.getJDA().getUserById(id);
            if (u != null) {
                return u;
            }
        } catch (ConversionException e) {
        }
        return context.getJDA().getUsersByName(arg, true).stream().findFirst().orElseThrow(ConversionException::new);
    }
}
