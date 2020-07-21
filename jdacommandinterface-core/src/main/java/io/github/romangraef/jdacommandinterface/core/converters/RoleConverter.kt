package io.github.romangraef.jdacommandinterface.core.converters;


import io.github.romangraef.jdacommandinterface.core.Context;
import io.github.romangraef.jdacommandinterface.core.ConversionException;
import net.dv8tion.jda.api.entities.Role;

public class RoleConverter implements Converter<Role> {
    public static final RoleConverter INSTANCE = new RoleConverter();

    protected RoleConverter() {
    }

    @Override
    public Role convert(Context context, String arg) throws ConversionException {
        try {
            long id = IDConverter.INSTANCE.convert(context, arg);
            Role r = context.getJDA().getRoleById(id);
            if (r != null) {
                return r;
            }
        } catch (ConversionException e) {
        }
        return context.getGuild().getRolesByName(arg, true).stream().findFirst().orElseThrow(ConversionException::new);
    }
}
