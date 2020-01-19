package io.github.romangraef.jdacommandinterface.paginator;

import net.dv8tion.jda.api.entities.Message;

public class ButtonMessage {
    private final long id;

    public ButtonMessage(long id) {
        this.id = id;
    }

    public ButtonMessage(Message message) {
        this(message.getIdLong());
    }

    public void addButton() {

    }

}
