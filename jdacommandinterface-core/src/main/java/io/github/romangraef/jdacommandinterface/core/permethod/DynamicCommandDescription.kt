package io.github.romangraef.jdacommandinterface.core.permethod

import io.github.romangraef.jdacommandinterface.core.ICommandDescription

data class DynamicCommandDescription(
        override val name: String,
        override val triggers: Array<String>,
        override val description: String,
        override val longDescription: String,
        override val hidden: Boolean,
        override val usage: Array<String>
) : ICommandDescription {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DynamicCommandDescription

        if (name != other.name) return false
        if (!triggers.contentEquals(other.triggers)) return false
        if (description != other.description) return false
        if (longDescription != other.longDescription) return false
        if (hidden != other.hidden) return false
        if (!usage.contentEquals(other.usage)) return false
        return true
    }

    override fun hashCode(): Int =
            arrayOf(name, triggers, description, longDescription, hidden, usage).contentDeepHashCode()

}