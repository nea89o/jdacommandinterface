package io.github.romangraef.jdacommandinterface.core.util;

import io.github.romangraef.jdacommandinterface.core.Context;
import io.github.romangraef.jdacommandinterface.core.ConversionException;
import io.github.romangraef.jdacommandinterface.core.NoConverterFoundException;
import io.github.romangraef.jdacommandinterface.core.NotEnoughArgumentsException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ArgumentUtilTest {

    public void parameterMethodISB(Context context, int a, String b, boolean c) {
    }

    @Test
    public void test() throws NoSuchMethodException, ConversionException, NoConverterFoundException, NotEnoughArgumentsException {
        Context context = mock(Context.class);
        Object[] parameters = ArgumentUtil.getArguments(context, new String[]{
                "0", "hello", "true"
        }, 3, false, ArgumentUtilTest.class.getMethod("parameterMethodISB", Context.class, Integer.TYPE, String.class, Boolean.TYPE));
        assertEquals("First parameter should be context", context, parameters[0]);
        assertEquals(0, parameters[1]);
        assertEquals("hello", parameters[2]);
        assertEquals(true, parameters[3]);
    }


}