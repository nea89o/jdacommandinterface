package io.github.romangraef.jdacommandinterface.core


@Retention(AnnotationRetention.RUNTIME)
annotation class Checks(val value: Array<Check> = [])