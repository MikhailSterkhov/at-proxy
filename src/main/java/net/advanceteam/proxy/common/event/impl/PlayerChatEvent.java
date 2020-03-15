package net.advanceteam.proxy.common.event.impl;

import lombok.Getter;
import lombok.Setter;
import net.advanceteam.proxy.common.event.ProxyEvent;
import net.advanceteam.proxy.common.event.cancel.Cancellable;
import net.advanceteam.proxy.connection.player.Player;

@Getter
@Setter
public class PlayerChatEvent extends ProxyEvent implements Cancellable {

    private boolean cancelled;

    private final Player player;
    private String message;

    public PlayerChatEvent(Player player, String message) {
        this.player = player;
        this.message = message;
    }

    public boolean isCommand() {
        return !message.isEmpty() && message.charAt(0) == '/';
    }

}
