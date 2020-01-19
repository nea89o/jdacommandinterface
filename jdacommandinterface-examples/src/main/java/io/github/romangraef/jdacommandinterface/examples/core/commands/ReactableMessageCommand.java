package io.github.romangraef.jdacommandinterface.examples.core.commands;

import io.github.romangraef.jdacommandinterface.core.Command;
import io.github.romangraef.jdacommandinterface.core.CommandDescription;
import io.github.romangraef.jdacommandinterface.core.Context;
import io.github.romangraef.jdacommandinterface.paginator.ReactableMessage;
import net.dv8tion.jda.api.entities.Message;

@CommandDescription(
        name = "reactable",
        triggers = {"react"},
        description = "Yeah just a message with a reactable icon",
        longDescription = "Just try it",
        usage = {
                "react"
        }
)
public class ReactableMessageCommand extends Command {
    public void execute(Context context) {
        Message mes = context.send("> haha click buttons").complete();
        ReactableMessage.watch(context, mes)
                .on("\uD83D\uDC9C", ev -> context.send("You clicked the heart").queue());
    }
}
