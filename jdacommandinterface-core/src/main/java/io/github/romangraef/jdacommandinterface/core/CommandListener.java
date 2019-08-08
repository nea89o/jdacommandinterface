package io.github.romangraef.jdacommandinterface.core;

import io.github.romangraef.jdacommandinterface.core.errors.CommandErrors;
import io.github.romangraef.jdacommandinterface.core.util.RandomUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandListener extends ListenerAdapter {
    private List<String> admins;
    private List<Command> commands;
    private Function<Message, String[]> prefixes;
    private CommandErrors errors;
    private boolean mentionPrefix;
    private List<Object> eventListeners = new ArrayList<>();

    public CommandListener(Function<Message, String[]> prefixes, boolean mentionPrefix, List<String> admins, CommandErrors errors, List<Command> commands) {
        this.prefixes = prefixes;
        this.admins = admins;
        this.errors = errors;
        this.mentionPrefix = mentionPrefix;
        this.commands = commands;
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    public void findCommands(String pack) {
        RandomUtil.findCommands(pack, commands);
    }

    @Override
    public void onReady(ReadyEvent event) {
        event.getJDA().retrieveApplicationInfo().queue(applicationInfo -> admins.add(applicationInfo.getOwner().getId()));
    }

    @Override
    public void onGenericEvent(@Nonnull GenericEvent event) {
        eventListeners.forEach(listener -> {
            try {
                listener.getClass().getMethod("onEvent", GenericEvent.class).invoke(listener, event);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String[] foundPrefixes = getPrefixes(event);
        String content = (event.getMessage().getContentRaw());
        Optional<String> anyPrefix = Stream.of(foundPrefixes).filter(content::startsWith).findAny();
        if (!anyPrefix.isPresent()) return;
        String prefix = anyPrefix.get();
        content = content.substring(prefix.length());
        String[] x = content.split(" ", 2);
        String command = x[0];
        String argText = "";
        if (x.length > 1) {
            argText = x[1];
        }
        Optional<Command> anyCommand = commands.stream()
                .filter(cmd -> cmd.isTrigger(command))
                .findAny();
        if (!anyCommand.isPresent()) return;
        Command cmd = anyCommand.get();
        String[] args;
        try {
            args = getArgs(cmd, argText);
        } catch (NotEnoughArgumentsException e) {
            Context ctx = new Context(this, event, null, prefix);
            ctx.send(new EmbedBuilder()
                    .setDescription("Missing arguments\n**Usage:**\n" + Arrays.stream(cmd.getDescription().usage()).map(y -> ctx.getPrefix() + y).collect(Collectors.joining("\n")))
                    .setFooter("[] = required | <> = optional", null)
                    .build()).queue();
            e.printStackTrace();
            return;
        }
        new Thread(() -> {
            Context ctx = new Context(this, event, args, prefix);
            List<Check> failedChecks = cmd.runChecks(ctx);
            if (!failedChecks.isEmpty()) {
                ctx.send(failedChecks.stream().map(Check::getDescription).collect(Collectors.joining("\n"))).queue();
                return;
            }
            try {
                cmd.runCommand(ctx, args);
            } catch (ConversionException e) {
                ctx.send(new EmbedBuilder()
                        .setDescription("Wrong argument passed\n**Usage:**\n" + Arrays.stream(cmd.getDescription().usage()).map(y -> ctx.getPrefix() + y).collect(Collectors.joining("\n")))
                        .setFooter("[] = required | <> = optional", null)
                        .build()).queue();
            } catch (Exception e) {
                captureError(ctx, e);

            }
        }).start();
    }

    private String[] getPrefixes(MessageReceivedEvent event) {
        ArrayList<String> pre = new ArrayList<>(Arrays.asList(prefixes.apply(event.getMessage())));
        if (this.mentionPrefix) {
            String selfId = event.getJDA().getSelfUser().getId();
            pre.add(String.format("<@%s> ", selfId));
            pre.add(String.format("<@!%s> ", selfId));
        }
        return pre.toArray(new String[0]);
    }

    private String[] getArgs(Command cmd, String argText) throws NotEnoughArgumentsException {
        String[] args;
        if (cmd.isVarArgs()) {
            args = argText.split("\\s+");
        } else
            args = argText.split("\\s+", cmd.getArgCount());
        args = Arrays.stream(args)
                .filter(x -> !x.isEmpty())
                .toArray(String[]::new);
        if (args.length < cmd.getArgCount()) {
            throw new NotEnoughArgumentsException();
        }
        return args;
    }

    public List<Command> getCommands() {
        return this.commands;
    }

    public List<Command> getVisibleCommands() {
        return getCommands().stream().filter(command -> !command.isHidden()).collect(Collectors.toList());
    }

    public List<String> getAdmins() {
        return admins;
    }

    public void captureError(Context ctx, Exception e) {
        if (errors.findHandler(e, ctx)) {
            return;
        }

        ctx.send(new EmbedBuilder()
                .setColor(Color.RED)
                .setDescription("There occurred an error during command execution")
                .setFooter("Please contact our developers.", null)
                .build()).queue();
    }

    /**
     * DO NOT USE THIS METHOD.
     */
    public void addEventListener(Object eventListener) {
        this.eventListeners.add(eventListener);
    }
}
