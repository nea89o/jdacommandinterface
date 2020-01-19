package io.github.romangraef.jdacommandinterface.paginator;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;

public interface MessageFormatter<T> {
    RestAction<Message> sendMessage(MessageChannel channel, T object);

    RestAction<Message> editMessage(Message message, T object);
}
