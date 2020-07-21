package io.github.romangraef.jdacommandinterface.core

import net.dv8tion.jda.api.Permission

enum class Check {
    DEVELOPER_ONLY {
        override fun check(context: Context): Boolean {
            return context.commandListener.admins.contains(context.author.id)
        }

        override val description: String = "Just for developer"
    },
    ADMIN_ONLY {
        override fun check(context: Context): Boolean {
            return DEVELOPER_ONLY.check(context) ||
                    context.member?.hasPermission(Permission.MANAGE_SERVER) ?: false
        }

        override val description: String =
                "For this command you need `MANAGE_SERVER` permissions on this discord to execute this command"

    };

    abstract fun check(context: Context): Boolean

    /**
     * Error description
     *
     * @return a generic description
     */
    abstract val description: String
}