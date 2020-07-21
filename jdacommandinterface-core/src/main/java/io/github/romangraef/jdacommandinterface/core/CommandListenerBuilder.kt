package io.github.romangraef.jdacommandinterface.core

import io.github.romangraef.jdacommandinterface.core.builtins.HelpCommand
import io.github.romangraef.jdacommandinterface.core.errors.CommandErrors
import io.github.romangraef.jdacommandinterface.core.util.RandomUtil
import net.dv8tion.jda.api.entities.Message
import java.util.ArrayList

class CommandListenerBuilder {
    private var prefixes: ((Message) -> Array<String>)? = null
    private var mentionPrefix = false
    private var admins: MutableList<String> = ArrayList()
    private val errors = CommandErrors()
    private val commands: MutableList<Command> = ArrayList()
    fun discoverErrorHandlers(packageName: String): CommandListenerBuilder {
        errors.discoverHandlers(packageName)
        return this
    }

    fun addErrorHandler(clazz: Class<out Throwable>, consumer: (Throwable, Context) -> Unit): CommandListenerBuilder {
        errors.addErrorHandler(clazz, consumer)
        return this
    }

    fun setAdmins(vararg admins: String): CommandListenerBuilder {
        return setAdmins(admins.toList())
    }

    fun addAdmins(vararg admins: String): CommandListenerBuilder {
        return addAdmins(admins.toList())
    }

    fun addAdmins(admins: List<String>): CommandListenerBuilder {
        this.admins.addAll(admins)
        return this
    }

    fun setAdmins(admins: List<String>): CommandListenerBuilder {
        this.admins.clear()
        this.addAdmins(admins)
        return this
    }

    fun setPrefixes(prefixes: (Message) -> Array<String>): CommandListenerBuilder {
        this.prefixes = prefixes
        return this
    }

    fun setPrefix(prefix: String): CommandListenerBuilder {
        return setPrefixes(prefix)
    }

    fun setPrefix(prefixes: (Message) -> String): CommandListenerBuilder {
        return setPrefixes { arrayOf(prefixes.invoke(it)) }
    }

    fun setAllowMentionPrefix(mentionPrefix: Boolean): CommandListenerBuilder {
        this.mentionPrefix = mentionPrefix
        return this
    }

    fun addCommand(command: Command): CommandListenerBuilder {
        commands.add(command)
        return this
    }

    fun findCommands(pack: String): CommandListenerBuilder {
        RandomUtil.findCommands(pack, commands)
        return this
    }

    fun build(): CommandListener {
        return CommandListener(prefixes
                ?: throw RuntimeException("Please provide a prefix"), mentionPrefix, admins, errors, commands)
    }

    fun setPrefixes(vararg prefixes: String): CommandListenerBuilder {
        this.prefixes = { _ -> prefixes.toList().toTypedArray() }
        return this
    }

    init {
        addCommand(HelpCommand)
    }
}