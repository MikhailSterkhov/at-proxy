package net.advanceteam.proxy.common.event.impl;

import lombok.Getter;
import lombok.Setter;
import net.advanceteam.proxy.common.event.ProxyEvent;
import net.advanceteam.proxy.common.event.cancel.Cancellable;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.connection.server.impl.Server;

@Getter
@Setter
public class ServerConnectEvent extends ProxyEvent implements Cancellable {

    private final Player player;

    private Server target;

    private boolean cancelled;

    private final Reason reason;


    public ServerConnectEvent(Player player, Server target) {
        this(player, target, Reason.UNKNOWN);
    }

    public ServerConnectEvent(Player player, Server target, Reason reason) {
        this.player = player;
        this.target = target;
        this.reason = reason;
    }


    public enum Reason {

        /**
         * Redirection to lobby server due to being unable to connect to
         * original server
         */
        LOBBY_FALLBACK,

        /**
         * Execution of a command
         */
        COMMAND,

        /**
         * Redirecting to another server when client loses connection to server
         * due to an exception.
         */
        SERVER_DOWN_REDIRECT,

        /**
         * Redirecting to another server when kicked from original server.
         */
        KICK_REDIRECT,

        /**
         * Plugin message request.
         */
        PLUGIN_MESSAGE,

        /**
         * Initial proxy connect.
         */
        JOIN_PROXY,

        /**
         * Plugin initiated connect.
         */
        PLUGIN,

        /**
         * Unknown cause.
         */
        UNKNOWN
    }
}
