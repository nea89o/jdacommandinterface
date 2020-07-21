package io.github.romangraef.jdacommandinterface.core.util;

import io.github.romangraef.jdacommandinterface.core.Command;
import io.github.romangraef.jdacommandinterface.core.CommandDescription;
import net.dv8tion.jda.api.entities.ISnowflake;
import org.reflections.Reflections;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class RandomUtil {

    public static ISnowflake createSnowflake(long id) {
        return () -> id;
    }

    public static long parseLongSafe(String text) {
        try {
            return Long.parseUnsignedLong(text);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }


    public static void findCommands(String pack, List<Command> commands) {
        Reflections reflections = new Reflections(pack);
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(CommandDescription.class)) {
            if (Command.class.isAssignableFrom(clazz)) {
                try {
                    Command command = (Command) clazz.newInstance();
                    commands.add(command);
                } catch (InstantiationException | IllegalAccessException e) {
                    getLogger("loader").error("Failed to create command " + clazz.getSimpleName(), e);
                }
            }
        }
    }
}
