package io.github.romangraef.jdacommandinterface.paginator;

import io.github.romangraef.jdacommandinterface.core.CommandListener;
import io.github.romangraef.jdacommandinterface.core.Context;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.requests.RestAction;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ReactableMessage {
    protected Message message;
    private CommandListener listener;
    private Map<String, Consumer<MessageReactionAddEvent>> emojiHandlers = new HashMap<>();
    private Map<Emote, Consumer<MessageReactionAddEvent>> emoteHandlers = new HashMap<>();

    protected ReactableMessage(Message message) {
        this.message = message;
    }

    protected ReactableMessage(RestAction<Message> sendMessage) {
        this(sendMessage.complete()); // TODO find better solution
    }


    public static ReactableMessage watch(Context context, Message message) {
        ReactableMessage reactableMessage = new ReactableMessage(message);
        reactableMessage.register(context.getCommandListener());
        return reactableMessage;
    }

    protected void register(CommandListener commandListener) {
        if (listener != null) {
            unregister();
        }
        commandListener.addEventListener(this);
        listener = commandListener;
    }

    protected void unregister() {
        listener.removeEventListener(this);
        listener = null;
    }

    private void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        if (event.getUser().isBot())
            return;
        if (event.getMessageIdLong() != message.getIdLong())
            return;
        MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();
        Consumer<MessageReactionAddEvent> handler;
        if (reactionEmote.isEmoji()) {
            handler = emojiHandlers.get(reactionEmote.getEmoji());
        } else {
            handler = emoteHandlers.get(reactionEmote.getEmote());
        }
        if (handler != null) {
            handler.accept(event);
        }
        event.getReaction().removeReaction(event.getUser()).queue();
    }

    public void onEvent(GenericEvent event) {
        if (event instanceof MessageReactionAddEvent) {
            onMessageReactionAdd((MessageReactionAddEvent) event);
        }
    }

    public void on(String emoji, Consumer<MessageReactionAddEvent> handler) {
        message.addReaction(emoji).queue();
        emojiHandlers.put(emoji, handler);
    }

    public void on(Emote emote, Consumer<MessageReactionAddEvent> handler) {
        message.addReaction(emote).queue();
        emoteHandlers.put(emote, handler);
    }
}
