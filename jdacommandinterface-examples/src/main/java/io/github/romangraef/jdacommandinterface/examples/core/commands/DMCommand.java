package io.github.romangraef.jdacommandinterface.examples.core.commands;

import io.github.romangraef.jdacommandinterface.core.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;


@CommandDescription(
        name = "dm",
        triggers = {"dm", "directmessage"},
        description = "Sends a direct message to someone",
        longDescription = "Send a direct message to your friends and family! Please don't abuse!",
        usage = {
                "ping @B1nzy ban me, daddy",
                "ping @romangraef Hey, how are you"
        }
)
@Checks({Check.DEVELOPER_ONLY})
public class DMCommand extends Command {
    public void execute(Context context, User to, String message) {
        to.openPrivateChannel().queue(
                channel -> channel.sendMessage(new EmbedBuilder()
                        .setColor(Color.cyan)
                        .setTitle(String.format("A message from %s:", context.getAuthor().getName()))
                        .setDescription(message)
                        .build()).queue());
        context.send(new EmbedBuilder()
                .setTitle("Message sent!")
                .setDescription("Thanks for using me!")
                .build()).queue();
    }
}
