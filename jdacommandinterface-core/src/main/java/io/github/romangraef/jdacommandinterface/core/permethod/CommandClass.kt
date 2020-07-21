package io.github.romangraef.jdacommandinterface.core.permethod

import io.github.romangraef.jdacommandinterface.core.Check
import io.github.romangraef.jdacommandinterface.core.Checks

class CommandClass {
    init {
        val clazz: Class<out CommandClass?> = javaClass
        val globalChecks: Array<Check> = clazz.getAnnotation(Checks::class.java).value
    }
}