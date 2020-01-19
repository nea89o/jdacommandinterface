package io.github.romangraef.jdacommandinterface.paginator;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;

public class RawMessageFormatter implements MessageFormatter<String> {
    public static RawMessageFormatter INSTANCE = new RawMessageFormatter();

    private RawMessageFormatter() {

    }

    @Override
    public RestAction<Message> sendMessage(MessageChannel channel, String object) {
        return channel.sendMessage(object);
    }

    @Override
    public RestAction<Message> editMessage(Message message, String object) {
        return message.editMessage(object);
    }
}
