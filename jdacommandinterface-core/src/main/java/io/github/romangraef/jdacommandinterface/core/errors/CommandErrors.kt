package io.github.romangraef.jdacommandinterface.core.errors

import io.github.romangraef.jdacommandinterface.core.Context
import org.reflections.Reflections
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType
import java.util.HashMap

class CommandErrors {
    private val errorHandlers: MutableMap<(Throwable) -> Boolean, (Throwable, Context) -> Unit> = HashMap()

    /**
     * Discovers and registers all [ErrorHandler]s in a package annotated with [RegisterErrorHandler].
     *
     *
     * This is not recursive, so searching `tdl.example.a` won't find handlers in `tdl.example.a.b`. This
     * finds all classes extending [ErrorHandler].
     *
     * @param packageName the package to search
     */
    fun discoverHandlers(packageName: String?) {
        val reflections = Reflections(packageName)
        for (clazz in reflections.getTypesAnnotatedWith(RegisterErrorHandler::class.java)) {
            if (ErrorHandler::class.java.isAssignableFrom(clazz)) {
                val throwableClass = (clazz.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<Throwable>
                try {
                    val eh = clazz.getConstructor().newInstance() as ErrorHandler<*>
                    addErrorHandler(throwableClass) { throwable, ctx -> eh.handle(throwable, ctx) }
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * This method should rather be called "handleException", but is for internal use only, so I won't rename it just
     * yet. This method will handle an exception and pass it to all Handlers handling this exception
     *
     * @param t       the caught exception
     * @param context the context in which the exception occurred
     *
     * @return whether the exception was handled
     */
    fun findHandler(t: Throwable, context: Context): Boolean {
        return errorHandlers.entries
                .filter { it.key.invoke(t) }
                .onEach { it.value.invoke(t, context) }
                .any()
    }

    fun addErrorHandler(clazz: Class<out Throwable>, consumer: (Throwable, Context) -> Unit) {
        errorHandlers[{ getCauseList(it).any { obj -> clazz.isInstance(obj) } }] = consumer
    }

    private fun getCauseList(t: Throwable): List<Throwable> =
            generateSequence(
                    t, {
                it.cause
            }
            ).toList()

}