package io.github.romangraef.jdacommandinterface.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class Context {
    private final MessageReceivedEvent event;
    private final CommandListener listener;
    private Message message;
    private String[] args;
    private String prefix;
    private Guild guild;

    public Context(CommandListener listener, MessageReceivedEvent event, String[] args, String prefix) {
        this.message = event.getMessage();
        this.args = args;
        this.listener = listener;
        this.event = event;
        this.prefix = prefix;
        if (event.isFromGuild()) {
            this.guild = event.getGuild();
        }
    }

    public CommandListener getCommandListener() {
        return listener;
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public String[] getArguments() {
        return args;
    }

    public Guild getGuild() {
        return guild;
    }

    public MessageAction send(MessageEmbed embed) {
        if (embed.getColorRaw() == Role.DEFAULT_COLOR_RAW) {
            embed = new EmbedBuilder(embed)
                    .setColor(0x36393f)
                    .build();
        }
        return message.getChannel().sendMessage(embed);
    }

    public MessageAction send(String text) {
        return message.getChannel().sendMessage(text);
    }

    public String getPrefix() {
        return prefix;
    }

    public User getAuthor() {
        return event.getAuthor();
    }

    public MessageChannel getChannel() {
        return event.getChannel();
    }

    public Member getMember() {
        if (getGuild() == null) return null;
        return getGuild().getMember(getAuthor());
    }

    public JDA getJDA() {
        return event.getJDA();
    }

    public Member getSelfMember() {
        if (getGuild() == null) return null;
        return getGuild().getSelfMember();
    }

    public void captureError(Exception e) {
        getCommandListener().captureError(this, e);
    }
}
