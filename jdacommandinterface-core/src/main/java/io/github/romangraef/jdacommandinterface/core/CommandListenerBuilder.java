package io.github.romangraef.jdacommandinterface.core;

import io.github.romangraef.jdacommandinterface.core.builtins.HelpCommand;
import io.github.romangraef.jdacommandinterface.core.errors.CommandErrors;
import io.github.romangraef.jdacommandinterface.core.util.RandomUtil;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CommandListenerBuilder {

    private Function<Message, String[]> prefixes;
    private boolean mentionPrefix;
    private List<String> admins = new ArrayList<>();
    private CommandErrors errors = new CommandErrors();
    private List<Command> commands = new ArrayList<>();

    public CommandListenerBuilder() {
        addCommand(new HelpCommand());
    }

    public CommandListenerBuilder discoverErrorHandlers(String packageName) {
        errors.discoverHandlers(packageName);
        return this;
    }

    public CommandListenerBuilder addErrorHandler(Class<? extends Throwable> clazz, BiConsumer<Throwable, Context> consumer) {
        errors.addErrorHandler(clazz, consumer);
        return this;
    }

    public CommandListenerBuilder setAdmins(String... admins) {
        return setAdmins(Arrays.asList(admins));
    }

    public CommandListenerBuilder addAdmins(String... admins) {
        return addAdmins(Arrays.asList(admins));
    }

    public CommandListenerBuilder addAdmins(List<String> admins) {
        this.admins.addAll(admins);
        return this;
    }

    public CommandListenerBuilder setAdmins(List<String> admins) {
        this.admins = admins;
        return this;
    }

    public CommandListenerBuilder setPrefixes(Function<Message, String[]> prefixes) {
        this.prefixes = prefixes;
        return this;
    }

    public CommandListenerBuilder setPrefix(String prefix) {
        return setPrefixes(prefix);
    }

    public CommandListenerBuilder setPrefix(Function<Message, String> prefixes) {
        return setPrefixes(message -> new String[]{prefixes.apply(message)});
    }

    public CommandListenerBuilder setAllowMentionPrefix(boolean mentionPrefix) {
        this.mentionPrefix = mentionPrefix;
        return this;
    }

    public CommandListenerBuilder addCommand(Command command) {
        commands.add(command);
        return this;
    }

    public CommandListenerBuilder findCommands(String pack) {
        RandomUtil.findCommands(pack, commands);
        return this;
    }

    public CommandListener build() {
        return new CommandListener(prefixes, mentionPrefix, admins, errors, commands);
    }

    public CommandListenerBuilder setPrefixes(String... prefixes) {
        this.prefixes = ignored -> prefixes;
        return this;
    }
}
