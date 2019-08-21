package io.github.romangraef.jdacommandinterface.core;

import io.github.romangraef.jdacommandinterface.core.errors.CommandErrors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

/**
 * Context for a command execution. It contains information on the originating message, the command listener and can be
 * used to respond to the event.
 */
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

    /**
     * @return the command listener instance that created this Context
     */
    public CommandListener getCommandListener() {
        return listener;
    }

    /**
     * @return the JDA event that caused the command to be executed
     */
    public MessageReceivedEvent getEvent() {
        return event;
    }

    /**
     * @return the arguments of this command excluding the command itself
     */
    public String[] getArguments() {
        return args;
    }

    /**
     * @return the guild this command was executed on, may be null
     */
    public Guild getGuild() {
        return guild;
    }

    /**
     * Sends an embed to the channel this command invocation was triggered in
     *
     * @param embed the embed to send
     *
     * @return an action which upon execution will send the embed
     */
    public MessageAction send(MessageEmbed embed) {
        if (embed.getColorRaw() == Role.DEFAULT_COLOR_RAW) {
            embed = new EmbedBuilder(embed)
                    .setColor(0x36393f)
                    .build();
        }
        return message.getChannel().sendMessage(embed);
    }

    /**
     * Sends a text to the channel this command invocation was triggered in
     *
     * @param text the text to send
     *
     * @return an action which upon execution will send the text
     */
    public MessageAction send(String text) {
        return message.getChannel().sendMessage(text);
    }

    /**
     * @return the prefix used for this command
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @return the user who used the command
     */
    public User getAuthor() {
        return event.getAuthor();
    }

    /**
     * @return the channel this command invocation was triggered in
     */
    public MessageChannel getChannel() {
        return event.getChannel();
    }

    /**
     * @return the user who used the command as a Member or null if this wasn't in a guild.
     */
    public Member getMember() {
        if (getGuild() == null) return null;
        return getGuild().getMember(getAuthor());
    }

    /**
     * This method returns the JDA instance that triggered this event. If you use sharding this might not contain all
     * information your bot has access to.
     *
     * @return the underlying jda.
     */
    public JDA getJDA() {
        return event.getJDA();
    }

    /**
     * @return the bot as a guild member or null if not in a guild
     */
    public Member getSelfMember() {
        if (getGuild() == null) return null;
        return getGuild().getSelfMember();
    }

    /**
     * Captures an error and sends it to the {@link CommandErrors} to handle.
     *
     * @param e the error to handle
     */
    public void captureError(Exception e) {
        getCommandListener().captureError(this, e);
    }
}
