## JDA Command Interface

Even though there are a lot of different Command handlers and interfaces, i decided to create my own framework. The ambition of this framework is to be somewhat close to [`discord.ext.commands`][discordpyext] from [discord.py][discordpy]

### Features
The whole library is currently split into three parts, which the individual user can mix and match.

The key feature i wanted to carry over from [`discord.ext.commands`][discordpyext] was the ability to define commands simply as a method, with an annotation and get automatic mapping to things like argument parsing and conversion to Java (or respectively Python) objects. For example: 

```java
@CommandDescription(
        name = "dm",
        triggers = {"dm", "directmessage"},
        description = "Sends a direct message to someone",
        longDescription = "Send a direct message to your friends and family! Please don't abuse!",
        usage = {
                "ping @B1nzy ban me, daddy",
                "ping @romangraef Hey, how are you"
        }
)
@Checks({Check.DEVELOPER_ONLY})
public class DMCommand extends Command {
    public void execute(Context context, User to, String message) {
        to.openPrivateChannel().queue(
                channel -> channel.sendMessage(new EmbedBuilder()
                        .setColor(Color.cyan)
                        .setTitle(String.format("A message from %s:", context.getAuthor().getName()))
                        .setDescription(message)
                        .build()).queue());
        context.send(new EmbedBuilder()
                .setTitle("Message sent!")
                .setDescription("Thanks for using me!")
                .build()).queue();
    }
}
```

As you can see there isn't really a lot of boilerplate code going on. All we really have in terms of boilerplate is the @CommandDescription annotation, which can be used with *only* the name parameter, the rest is only there to customize the help. The corresponding main isn't much longer:

```java

public class Main {
    public static void main(String[] args) throws LoginException {
        CommandListener commandListener = new CommandListenerBuilder()
                .setPrefix("+")
                .findCommands("io.github.romangraef.jdacommandinterface.examples.core.commands") // finds all commands in that package
                .build();

        JDA jda = new JDABuilder()
                .setToken(System.getenv("TOKEN"))
                .addEventListeners(commandListener)
                .build();

    }
}
```
It doesn't get much simpler than that!



[discordpy]: https://github.com/Rapptz/discord.py/
[discordpyext]: https://discordpy.readthedocs.io/en/latest/ext/commands/index.html