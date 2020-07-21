package io.github.romangraef.jdacommandinterface.core

class NoConverterFoundException(clazz: Class<*>) : Exception("No converter found for class: " + clazz.canonicalName)