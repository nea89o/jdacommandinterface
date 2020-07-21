package io.github.romangraef.jdacommandinterface.core

import io.github.romangraef.jdacommandinterface.core.errors.CommandErrors
import io.github.romangraef.jdacommandinterface.core.util.RandomUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.ApplicationInfo
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color
import java.lang.reflect.InvocationTargetException
import java.util.ArrayList
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Stream
import javax.annotation.Nonnull
import kotlin.concurrent.thread

class CommandListener(val prefixes: (Message)-> Array<String>, val mentionPrefix: Boolean, val admins: MutableList<String>, private val errors: CommandErrors, private val commands: MutableList<Command>) : ListenerAdapter() {
    private val eventListeners: MutableList<Any> = ArrayList()
    fun addCommand(command: Command) {
        commands.add(command)
    }

    fun findCommands(pack: String) {
        RandomUtil.findCommands(pack, commands)
    }

    override fun onReady(event: ReadyEvent) {
        event.jda.retrieveApplicationInfo().queue {
            admins.add(it.owner.id)
            it.team?.members?.forEach { admins.add(it.user.id) }
        }
    }

    override fun onGenericEvent(@Nonnull event: GenericEvent) {
        eventListeners.forEach(Consumer { listener: Any ->
            try {
                val method = listener.javaClass.getMethod("onEvent", GenericEvent::class.java)
                method.isAccessible = true
                method.invoke(listener, event)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            }
        })
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        val foundPrefixes = getPrefixes(event)
        var content = event.message.contentRaw
        val anyPrefix = Stream.of(*foundPrefixes).filter { prefix: String? -> content.startsWith(prefix!!) }.findAny()
        if (!anyPrefix.isPresent) return
        val prefix = anyPrefix.get()
        content = content.substring(prefix.length)
        val x = content.split("\\s".toRegex(), 2).toTypedArray()
        val command = x[0]
        var argText = ""
        if (x.size > 1) {
            argText = x[1]
        }
        val anyCommand = commands.stream()
                .filter { it.isTrigger(command) }
                .findAny()
        if (!anyCommand.isPresent) return
        val cmd = anyCommand.get()
        val args = try {
            getArgs(cmd, argText)
        } catch (e: NotEnoughArgumentsException) {
            val ctx = Context(this, event, arrayOf(), prefix)
            ctx.send(EmbedBuilder()
                    .setDescription("Missing arguments\n" +
                            "**Usage:**\n" +
                            cmd.description.usage.joinToString(separator = "\n") {
                                ctx.prefix + it
                            })
                    .setFooter("[] = required | <> = optional", null)
                    .build()).queue()
            e.printStackTrace()
            return
        }
        thread(start = true) {
            val ctx = Context(this, event, args, prefix)
            val failedChecks = cmd.runChecks(ctx)
            if (failedChecks.isNotEmpty()) {
                ctx.send(failedChecks.joinToString(separator = "\n") { it.description }).queue()
                return@thread
            }
            try {
                cmd.runCommand(ctx, args)
            } catch (e: ConversionException) {
                ctx.send(EmbedBuilder()
                        .setDescription("""
                        |Wrong argument passed
                        |**Usage:**
                        |${cmd.description.usage.joinToString(separator = "\n") { ctx.prefix + it }}
                """.trimMargin())
                        .setFooter("[] = required | <> = optional", null)
                        .build()).queue()
            } catch (e: Exception) {
                captureError(ctx, e)
            }
        }
    }

    private fun getPrefixes(event: MessageReceivedEvent): Array<String> {
        val pre = prefixes.invoke(event.message).toMutableList()
        if (mentionPrefix) {
            val selfId = event.jda.selfUser.id
            pre.add(String.format("<@%s> ", selfId))
            pre.add(String.format("<@!%s> ", selfId))
        }
        return pre.toTypedArray()
    }

    @Throws(NotEnoughArgumentsException::class)
    private fun getArgs(cmd: Command, argText: String): Array<String> {
        val args = if (cmd.isVarArgs) {
            argText.split("\\s+".toRegex())
        } else {
            argText.split("\\s+".toRegex(),
                    cmd.argCount.coerceAtLeast(0))
        }.filter { !it.isBlank() }.toTypedArray()
        if (args.size < cmd.argCount) {
            throw NotEnoughArgumentsException()
        }
        return args
    }

    val visibleCommands: List<Command>
        get() = commands.filter { !it.isHidden }

    fun captureError(ctx: Context, e: Exception) {
        if (errors.findHandler(e, ctx)) {
            return
        }
        e.printStackTrace()
        ctx.send(EmbedBuilder()
                .setColor(Color.RED)
                .setDescription("There occurred an error during command execution")
                .setFooter("Please contact our developers.", null)
                .build()).queue()
    }

    /**
     * DO NOT USE THIS METHOD.
     */
    fun addEventListener(eventListener: Any) {
        eventListeners.add(eventListener)
    }

    /**
     * DO NOT USE THIS METHOD.
     */
    fun removeEventListener(eventListener: Any?) {
        eventListeners.remove(eventListener)
    }

}