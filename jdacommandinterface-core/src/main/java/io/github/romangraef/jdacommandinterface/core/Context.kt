package io.github.romangraef.jdacommandinterface.core

import io.github.romangraef.jdacommandinterface.core.errors.CommandErrors
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.requests.restaction.MessageAction

/**
 * Context for a command execution. It contains information on the originating message, the command listener and can be
 * used to respond to the event.
 */
class Context(val commandListener: CommandListener,
              val event: MessageReceivedEvent,
              val arguments: Array<String>,
              val prefix: String) {

    private val message: Message = event.message

    /**
     * @return the guild this command was executed on, may be null
     */
    val guild: Guild? = message.guild

    /**
     * Sends an embed to the channel this command invocation was triggered in
     *
     * @param embed the embed to send
     *
     * @return an action which upon execution will send the embed
     */
    fun send(embed: MessageEmbed): MessageAction {
        return message.channel.sendMessage(embed)
    }

    /**
     * Sends a text to the channel this command invocation was triggered in
     *
     * @param text the text to send
     *
     * @return an action which upon execution will send the text
     */
    fun send(text: String): MessageAction {
        return message.channel.sendMessage(text)
    }

    /**
     * the user who used the command
     */
    val author: User = event.author

    /**
     * the channel this command invocation was triggered in
     */
    val channel: MessageChannel = event.channel

    /**
     * @return the user who used the command as a Member or null if this wasn't in a guild.
     */
    val member: Member? by lazy { guild?.getMember(author) }

    /**
     * This method returns the JDA instance that triggered this event. If you use sharding this might not contain all
     * information your bot has access to.
     *
     * @return the underlying jda.
     */
    val jda: JDA = event.jda

    /**
     * @return the bot as a guild member or null if not in a guild
     */
    val selfMember: Member? = guild?.selfMember

    /**
     * Captures an error and sends it to the [CommandErrors] to handle.
     *
     * @param e the error to handle
     */
    fun captureError(e: Exception) {
        commandListener.captureError(this, e)
    }
}