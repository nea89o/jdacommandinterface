package io.github.romangraef.jdacommandinterface.awaiter;

import io.github.romangraef.jdacommandinterface.core.Context;
import io.github.romangraef.jdacommandinterface.core.ConversionException;
import io.github.romangraef.jdacommandinterface.core.NoConverterFoundException;
import io.github.romangraef.jdacommandinterface.core.converters.Converter;
import io.github.romangraef.jdacommandinterface.core.converters.Converters;
import io.github.romangraef.jdacommandinterface.core.util.FutureUtil;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;

import java.awt.Color;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class AdvancedWaiter implements AutoCloseable {
    private static final long DEFAULT_TIMEOUT = 120L;
    private final User user;
    private final EventWaiterListener waiter;
    private final MessageChannel channel;
    private final long timeout;
    private final Context context;

    private AdvancedWaiter(Context context, User user, MessageChannel channel, long timeout) {
        checkState();
        this.context = context;
        waiter = new EventWaiterListener(Thread::new, context.getJDA());
        this.user = user;
        this.channel = channel;
        this.timeout = timeout;
    }

    public static AdvancedWaiter fromContext(Context context, long timeout) {
        return new AdvancedWaiter(context, context.getAuthor(), context.getChannel(), timeout);
    }

    public static AdvancedWaiter fromContext(Context context) {
        return fromContext(context, DEFAULT_TIMEOUT);
    }


    private void checkState() {
    }

    public CompletableFuture<Message> waitForMessage() {
        return waitForConvertable(new Converter<Message>() {
            @Override
            public Message convert(Context ctx, String arg) throws ConversionException {
                return null;
            }

            @Override
            public Message convert(Context ctx, Message mes) throws ConversionException {
                return mes;
            }
        }, ignored -> true);
    }

    public CompletableFuture<Role> waitForRole(Predicate<Role> check) {
        try {
            return waitForConvertable(Role.class, check);
        } catch (NoConverterFoundException e) {
            context.captureError(e);
            return FutureUtil.reject(e);
        }
    }

    public CompletableFuture<Role> waitForRole() {
        return waitForRole(ignored -> true);
    }

    private <T> CompletableFuture<T> waitForConvertable(Class<T> clazz, Predicate<T> check) throws NoConverterFoundException {
        return waitForConvertable(Converters.findConverter(clazz), check);
    }

    public CompletableFuture<MessageReaction> waitForReaction(Message base) {
        return waitForReaction(base, ignored -> true);
    }

    public CompletableFuture<MessageReaction> waitForReaction(Message base, Predicate<MessageReaction> reaction) {
        CompletableFuture<MessageReaction> fut = new CompletableFuture<>();
        waiter.waitForEvent(GenericMessageReactionEvent.class, event -> {
            if (event.getUser().getIdLong() != user.getIdLong() || base.getIdLong() != event.getMessageIdLong()) {
                return false;
            }
            return reaction.test(event.getReaction());
        }, e -> fut.complete(e.getReaction()));
        return fut;
    }

    public <T> CompletableFuture<T> waitForConvertable(Converter<T> converter, Predicate<T> check) {
        checkState();
        CompletableFuture<T> fut = new CompletableFuture<>();
        AtomicReference<T> reference = new AtomicReference<>();
        waiter.waitForEvent(MessageReceivedEvent.class,
                m -> {
                    if (!(m.getAuthor() == this.user && m.getChannel().getIdLong() == channel.getIdLong())) {
                        return false;
                    }
                    try {
                        T obj = converter.convert(this.context, m.getMessage());
                        if (check.test(obj)) {
                            reference.set(obj);
                            return true;
                        } else {
                            context.send("Looks like your input doesn't match the inquired item.").queue();
                        }
                    } catch (ConversionException ignored) {
                        context.send("Looks like your input doesn't match the inquired item.").queue();
                    }
                    return false;
                },
                e -> fut.complete(reference.get()),
                timeout, TimeUnit.SECONDS, () -> fut.completeExceptionally(new TimeoutException()));
        return fut;
    }

    public CompletableFuture<String> waitForText(Predicate<String> check) {
        try {
            return waitForConvertable(String.class, check);
        } catch (NoConverterFoundException e) {
            context.captureError(e);
            return FutureUtil.reject(e);
        }
    }

    public CompletableFuture<String> waitForText() {
        return waitForText(ignored -> true);
    }

    public CompletableFuture<TextChannel> waitForChannel(Predicate<TextChannel> check) {
        try {
            return waitForConvertable(TextChannel.class, check);
        } catch (NoConverterFoundException e) {
            context.captureError(e);
            return FutureUtil.reject(e);
        }
    }

    public CompletableFuture<TextChannel> waitForChannel() {
        return waitForChannel(ignored -> true);
    }

    public <T> CompletableFuture<T> waitForConvertable(Class<T> clazz) throws NoConverterFoundException {
        return waitForConvertable(clazz, ignored -> true);
    }

    public CompletableFuture<Boolean> waitForBool() {
        return waitForBool(ignored -> true);
    }

    public CompletableFuture<Boolean> waitForBool(Predicate<Boolean> check) {
        try {
            return waitForConvertable(Boolean.class, check);
        } catch (NoConverterFoundException e) {
            context.captureError(e);
            return FutureUtil.reject(e);
        }
    }

    public CompletableFuture<Color> waitForColor() {
        return waitForColor(ignored -> true);
    }

    public CompletableFuture<Color> waitForColor(Predicate<Color> check) {
        try {
            return waitForConvertable(Color.class, check);
        } catch (NoConverterFoundException e) {
            context.captureError(e);
            return FutureUtil.reject(e);
        }
    }

    public CompletableFuture<Long> waitForLong() {
        return waitForLong(i -> true);
    }

    public CompletableFuture<Long> waitForLong(Predicate<Long> check) {
        try {
            return waitForConvertable(Long.class, check);
        } catch (NoConverterFoundException e) {
            context.captureError(e);
            return FutureUtil.reject(e);
        }
    }

    @Override
    public void close() {
        waiter.close();
    }
}
