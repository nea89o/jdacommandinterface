package io.github.romangraef.jdacommandinterface.awaiter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EventWaiterListener implements Closeable {
    private final JDA jda;
    private ThreadFactory threadFactory;
    private List<Task<?>> tasks = Collections.synchronizedList(new ArrayList<>());

    public EventWaiterListener(ThreadFactory threadFactory, JDA jda) {
        this.threadFactory = threadFactory;
        jda.addEventListener(this);
        this.jda = jda;
    }

    @Override
    public void close() {
        jda.removeEventListener(this);
    }

    public final void onEvent(GenericEvent event) {
        ListIterator<Task<?>> tI = tasks.listIterator();
        while (tI.hasNext()) {
            Task<?> task = tI.next();
            synchronized (task) {
                if (task.isCancelled())
                    tI.remove();
                Class<?> eventClass = task.getEventClass();
                if (!eventClass.isAssignableFrom(event.getClass())) {
                    continue;
                }
                if (!task.test(event)) {
                    continue;
                }
                task.done(event);
            }
        }
    }

    public <E extends GenericEvent> Task<E> waitForEvent(Class<? extends E> eventClass, Predicate<E> predicate, Consumer<E> done) {
        Task<E> task = new Task<>(eventClass, predicate, done);
        tasks.add(task);
        return task;
    }

    public <E extends GenericEvent> Task<E> waitForEvent(Class<? extends E> eventClass, Predicate<E> predicate, Consumer<E> done, long timeout, TimeUnit unit, Runnable timeouted) {
        Task<E> task = waitForEvent(eventClass, predicate, done);

        Thread thread = threadFactory.newThread(() -> {
            try {
                unit.sleep(timeout);
                synchronized (task) {
                    task.cancel();
                    timeouted.run();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
        return task;
    }

    private static class Task<E extends GenericEvent> {

        private final Class<? extends E> eventClass;
        private final Predicate<E> predicate;
        private final Consumer<E> done;
        private boolean isCancelled = false;

        public Task(Class<? extends E> eventClass, Predicate<E> predicate, Consumer<E> done) {
            this.eventClass = eventClass;
            this.predicate = predicate;
            this.done = done;
        }

        public Class<? extends E> getEventClass() {
            return eventClass;
        }

        public Predicate<E> getPredicate() {
            return predicate;
        }

        public boolean test(GenericEvent event) {
            return predicate.test((E) event);
        }

        public Consumer<E> getDone() {
            return done;
        }

        public boolean isCancelled() {
            return isCancelled;
        }

        public void cancel() {
            isCancelled = true;
        }

        public void done(GenericEvent event) {
            done.accept((E) event);
            cancel();
        }
    }
}
