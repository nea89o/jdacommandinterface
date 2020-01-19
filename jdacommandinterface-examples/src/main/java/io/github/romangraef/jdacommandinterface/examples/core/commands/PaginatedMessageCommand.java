package io.github.romangraef.jdacommandinterface.examples.core.commands;

import io.github.romangraef.jdacommandinterface.core.Command;
import io.github.romangraef.jdacommandinterface.core.CommandDescription;
import io.github.romangraef.jdacommandinterface.core.Context;
import io.github.romangraef.jdacommandinterface.paginator.ArrayPageSupplier;
import io.github.romangraef.jdacommandinterface.paginator.RawMessageFormatter;


@CommandDescription(
        name = "page",
        triggers = {"page"},
        description = "Sends a paginated message",
        longDescription = "Send a paginated message to this channel",
        usage = {
                "page"
        }
)

public class PaginatedMessageCommand extends Command {
    public void execute(Context context) {
        io.github.romangraef.jdacommandinterface.paginator.PaginatedMessage.sendPaginatedMessage(context, new ArrayPageSupplier("A", "b", "c"), RawMessageFormatter.INSTANCE);
    }
}
