package io.github.romangraef.jdacommandinterface.core.errors;

import io.github.romangraef.jdacommandinterface.core.Context;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class CommandErrors {

    private Map<Predicate<Throwable>, BiConsumer<Throwable, Context>> errorHandlers = new HashMap<>();

    private static List<Throwable> getCauseList(Throwable t) {
        List<Throwable> throwables = new ArrayList<>();
        do {
            throwables.add(t);
        } while ((t = t.getCause()) != null);
        return throwables;
    }

    /**
     * Discovers and registers all {@link ErrorHandler}s in a package annotated with {@link RegisterErrorHandler}.
     * <p>
     * This is not recursive, so searching {@code tdl.example.a} won't find handlers in {@code tdl.example.a.b}. This
     * finds all classes extending {@link ErrorHandler}.
     *
     * @param packageName the package to search
     */
    @SuppressWarnings({"unsafe", "unchecked"})
    public void discoverHandlers(String packageName) {
        Reflections reflections = new Reflections(packageName);
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(RegisterErrorHandler.class)) {
            if (ErrorHandler.class.isAssignableFrom(clazz)) {
                Class<? extends Throwable> throwableClass = (Class<? extends Throwable>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
                try {
                    addErrorHandler(throwableClass, ((ErrorHandler) clazz.getConstructor().newInstance())::handle);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
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
    public boolean findHandler(Throwable t, Context context) {
        return errorHandlers.entrySet()
                .stream()
                .filter(entry -> entry.getKey().test(t))
                .peek(entry -> entry.getValue().accept(t, context))
                .findAny()
                .isPresent();
    }

    public void addErrorHandler(Class<? extends Throwable> clazz, BiConsumer<Throwable, Context> consumer) {
        errorHandlers.put(throwable -> getCauseList(throwable).stream().anyMatch(clazz::isInstance), consumer);
    }


}
