package io.github.romangraef.jdacommandinterface.paginator;

import io.github.romangraef.jdacommandinterface.core.Context;
import net.dv8tion.jda.api.entities.MessageChannel;

public class PaginatedMessage<T> extends ReactableMessage {
    private static final String FIRST_BUTTON = "\u23ea";
    private static final String BACK_BUTTON = "\u25c0";
    private static final String NEXT_BUTTON = "\u25b6\ufe0f";
    private static final String LAST_BUTTON = "\u23e9";
    private final MessageChannel channel;
    private final PageSupplier<T> supplier;
    private final MessageFormatter<T> formatter;
    private int page = 0;
    private int renderedPage = 0;

    protected PaginatedMessage(MessageChannel channel, PageSupplier<T> supplier, MessageFormatter<T> formatter) {
        super(formatter.sendMessage(channel, supplier.supplyPage(0)));
        this.channel = channel;
        this.supplier = supplier;
        this.formatter = formatter;
        on(FIRST_BUTTON, ignored -> setPage(supplier.getFirstIndex()));
        on(BACK_BUTTON, ignored -> decrementPage());
        on(NEXT_BUTTON, ignored -> incrementPage());
        on(LAST_BUTTON, ignored -> setPage(supplier.getLastIndex()));
    }

    public static <T> PaginatedMessage sendPaginatedMessage(Context context, PageSupplier<T> pageSupplier, MessageFormatter<T> formatter) {
        PaginatedMessage<T> tPaginatedMessage = new PaginatedMessage<T>(context.getChannel(), pageSupplier, formatter);
        tPaginatedMessage.register(context.getCommandListener());
        return tPaginatedMessage;
    }

    public void setPage(int index) {
        page = index;
        clampPage();
        rerender();
    }

    public void incrementPage() {
        page++;
        clampPage();
        rerender();
    }

    public void decrementPage() {
        page--;
        clampPage();
        rerender();
    }

    private void rerender() {
        if (renderedPage == page)
            return;
        renderedPage = page;
        formatter.editMessage(message, supplier.supplyPage(page)).queue();
    }

    private void clampPage() {
        if (page > supplier.getLastIndex()) {
            page = supplier.getLastIndex();
        }
        if (page < supplier.getFirstIndex()) {
            page = supplier.getFirstIndex();
        }
    }
}
