package io.github.romangraef.jdacommandinterface.listeners;

import io.github.romangraef.jdacommandinterface.core.CommandListener;
import net.dv8tion.jda.api.JDA;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterListeners {
    public static List<Object> registerListeners(String pack, Map<Class<?>, Object> injections) {
        List<Object> listeners = new ArrayList<>();
        for (Class<?> clazz : new Reflections(pack).getTypesAnnotatedWith(RegisterListener.class)) {
            listeners.add(newInstance(clazz, injections));
        }
        return listeners;
    }

    public static void registerListeners(String pack, JDA jda) {
        registerListeners(pack, new HashMap<Class<?>, Object>() {{
            put(JDA.class, jda);
        }}).forEach(jda::addEventListener);
    }

    public static void registerListeners(String pack, CommandListener commandListener) {
        registerListeners(pack, new HashMap<Class<?>, Object>() {{
            put(CommandListener.class, commandListener);
        }}).forEach(commandListener::addEventListener);
    }


    private static Object newInstance(Class<?> clazz, Map<Class<?>, Object> injections) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            try {
                switch (constructor.getParameterCount()) {
                    case 0:
                        constructor.setAccessible(true);
                        return constructor.newInstance();
                    case 1:
                        Class<?> parameterType = constructor.getParameterTypes()[0];
                        for (Map.Entry<Class<?>, Object> entry : injections.entrySet()) {
                            if (parameterType.isAssignableFrom(entry.getKey())) {
                                constructor.setAccessible(true);
                                return constructor.newInstance(entry.getValue());
                            }
                        }
                        break;
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
