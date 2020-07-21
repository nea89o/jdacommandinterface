package io.github.romangraef.jdacommandinterface.core.util;

import io.github.romangraef.jdacommandinterface.core.Context;
import io.github.romangraef.jdacommandinterface.core.ConversionException;
import io.github.romangraef.jdacommandinterface.core.NoConverterFoundException;
import io.github.romangraef.jdacommandinterface.core.NotEnoughArgumentsException;
import io.github.romangraef.jdacommandinterface.core.converters.Converters;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ArgumentUtil {
    /**
     * For internal use. Converts a string list into a list of objects according to a method definition.
     *
     * if varargs is set to true the last element of the result will be an array of those converted varargs
     *
     * @param context   the context of the command as passed to the {@link Converters}
     * @param args      the arguments to convert
     * @param argCount  the argument count of the method definition excluding {@link Context} and varargs
     * @param isVarArgs whether the method has varargs.
     * @param method    the method definition to use
     *
     * @return the arguments converted to objects.
     * @throws NoConverterFoundException if the method asks for an object without a registered converter
     * @throws ConversionException this is reraised from the converters and indicated some kind of user error
     * @throws NotEnoughArgumentsException if the provided arguments aren't enough to fulfill the needs of the method.
     */
    public static Object[] getArguments(Context context, String[] args, int argCount, boolean isVarArgs, Method method) throws NoConverterFoundException, ConversionException, NotEnoughArgumentsException {
        Object[] finalArgs = new Object[argCount + 1 + (isVarArgs ? 1 : 0)];
        finalArgs[0] = context;
        Object[] normalArgs = getConvertedNormalArgs(args, argCount, method, context);
        System.arraycopy(normalArgs, 0, finalArgs, 1, normalArgs.length);
        if (isVarArgs) {
            Parameter p = null;
            for (Parameter pa : method.getParameters()) {
                if (pa.isVarArgs()) {
                    p = pa;
                    break;
                }
            }
            Class<?> clazz = p.getType().getComponentType();
            Object[] varArgs = (Object[]) Array.newInstance(clazz, args.length - argCount);

            for (int i = argCount, j = 0; j < args.length - argCount; i++, j++) {
                varArgs[j] = Converters.convert(clazz, context, args[i]);
            }
            finalArgs[argCount + 1] = varArgs;
        }
        return finalArgs;
    }

    private static Object[] getConvertedNormalArgs(String[] args, int argCount, Method method, Context context) throws NoConverterFoundException, ConversionException, NotEnoughArgumentsException {
        Object[] converted = new Object[argCount];
        Parameter[] parameters = method.getParameters();
        for (int i = 1; i < parameters.length; i++) {
            Parameter p = parameters[i];
            if (p.isVarArgs()) continue;
            try {
                converted[i - 1] = Converters.convert(p.getType(), context, args[i - 1]);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NotEnoughArgumentsException(e);
            }
        }
        return converted;
    }
}
