package io.github.romangraef.jdacommandinterface.core.util

import io.github.romangraef.jdacommandinterface.core.Command
import io.github.romangraef.jdacommandinterface.core.CommandDescription
import net.dv8tion.jda.api.entities.ISnowflake
import org.reflections.Reflections
import org.slf4j.LoggerFactory

object RandomUtil {
    fun createSnowflake(id: Long): ISnowflake {
        return ISnowflake { id }
    }

    fun parseLongSafe(text: String): Long = try {
        text.toLong()
    } catch (e: NumberFormatException) {
        0L
    }

    fun findCommands(pack: String, commands: MutableList<Command>) {
        val reflections = Reflections(pack)
        for (clazz in reflections.getTypesAnnotatedWith(CommandDescription::class.java)) {
            if (Command::class.java.isAssignableFrom(clazz)) {
                try {
                    val command = clazz.newInstance() as Command
                    commands.add(command)
                } catch (e: InstantiationException) {
                    LoggerFactory.getLogger("loader").error("Failed to create command " + clazz.simpleName, e)
                } catch (e: IllegalAccessException) {
                    LoggerFactory.getLogger("loader").error("Failed to create command " + clazz.simpleName, e)
                }
            }
        }
    }
}