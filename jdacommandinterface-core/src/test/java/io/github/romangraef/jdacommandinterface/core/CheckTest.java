package io.github.romangraef.jdacommandinterface.core;

import net.dv8tion.jda.api.entities.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CheckTest {

    final String LOCAL_ADMIN_ID = "0987654321";
    final String ADMIN_ID = "1234567890";
    Context randomUserContext = mock(Context.class);
    Context adminUserContext = mock(Context.class);
    Context localAdminUserContext = mock(Context.class);
    User randomUser = mock(User.class);
    User admin = mock(User.class);
    User localAdmin = mock(User.class);
    CommandListener listener = mock(CommandListener.class);

    @Before
    public void setUp() {
        when(randomUserContext.getAuthor()).thenReturn(randomUser);
        when(randomUser.getId()).thenReturn("1212121212121212");
        when(localAdminUserContext.getAuthor()).thenReturn(localAdmin);
        when(localAdmin.getId()).thenReturn(LOCAL_ADMIN_ID);
        when(adminUserContext.getAuthor()).thenReturn(admin);
        when(admin.getId()).thenReturn(ADMIN_ID);
        when(randomUserContext.getCommandListener()).thenReturn(listener);
        when(adminUserContext.getCommandListener()).thenReturn(listener);
        when(localAdminUserContext.getCommandListener()).thenReturn(listener);
        when(listener.getAdmins()).thenReturn(Collections.singletonList(ADMIN_ID));
    }

    @Test
    public void checkDeveloperOnly() {
        Assert.assertFalse("Random users shouldn't be admins", Check.DEVELOPER_ONLY.check(randomUserContext));
        Assert.assertTrue("Admins should be admins.", Check.DEVELOPER_ONLY.check(adminUserContext));
        Assert.assertFalse("Local admins shouldn't be global admins", Check.DEVELOPER_ONLY.check(localAdminUserContext));
        Assert.assertNotNull("Description shouldn't be null", Check.DEVELOPER_ONLY.getDescription());
    }
}