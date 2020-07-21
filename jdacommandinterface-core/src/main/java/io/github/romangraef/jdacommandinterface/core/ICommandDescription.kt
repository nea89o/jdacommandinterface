package io.github.romangraef.jdacommandinterface.core

import io.github.romangraef.jdacommandinterface.core.permethod.DynamicCommandDescription

interface ICommandDescription {
    val name: String
    val triggers: Array<String>
    val description: String
    val longDescription: String
    val hidden: Boolean
    val usage: Array<String>
}

val CommandDescription.asICommandDescription
    get() = DynamicCommandDescription(
            name, triggers, description, longDescription, hidden, usage
    )