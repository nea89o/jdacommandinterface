package io.github.romangraef.jdacommandinterface.core.builtins

import io.github.romangraef.jdacommandinterface.core.Command
import io.github.romangraef.jdacommandinterface.core.CommandDescription
import io.github.romangraef.jdacommandinterface.core.Context
import net.dv8tion.jda.api.EmbedBuilder

/**
 * The default implementation for the help command.
 */
@CommandDescription(name = "Help", triggers = ["help", "?"], usage = ["help", "help <command>"], description = "Shows this message", longDescription = "This just shows you the help message")
object HelpCommand : Command() {
    fun execute(ctx: Context, vararg commands: String) {
        val eb = commands.firstOrNull()?.let { search ->
            val cmd = ctx.commandListener.visibleCommands
                    .firstOrNull { it.isTrigger(search) }
            if (cmd == null) {
                ctx.send(EmbedBuilder()
                        .setDescription("Command not found.")
                        .build()).queue()
                return
            }
            EmbedBuilder()
                    .setTitle(cmd.name)
                    .addField("Usage", cmd.usage.joinToString(separator = "\n") {
                        ctx.prefix + it
                    }, true)
                    .setDescription(cmd.longDescription)
                    .setFooter("<> = required | [] = optional", null)

        } ?: EmbedBuilder()
                .setTitle("Help")
                .setDescription(ctx.commandListener.visibleCommands
                        .joinToString(separator = "\n") {
                            "**${it.name}** - ${it.shortDescription}"
                        })
        ctx.send(eb.build()).queue()
    }
}