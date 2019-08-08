package io.github.romangraef.jdacommandinterface.examples.core;

import io.github.romangraef.jdacommandinterface.core.CommandListener;
import io.github.romangraef.jdacommandinterface.core.CommandListenerBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) throws LoginException {
        CommandListener commandListener = new CommandListenerBuilder()
                .addAdmins("310702108997320705") // Bot owner is automatically added as admin.
                .setPrefix("+")
                .findCommands("io.github.romangraef.jdacommandinterface.examples.core.commands")
                .build();

        JDA jda = new JDABuilder()
                .setToken(System.getenv("TOKEN"))
                .addEventListeners(commandListener)
                .build();

    }
}
