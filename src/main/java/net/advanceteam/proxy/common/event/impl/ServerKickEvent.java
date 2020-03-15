package net.advanceteam.proxy.common.event.impl;

import lombok.Getter;
import lombok.Setter;
import net.advanceteam.proxy.common.chat.component.BaseComponent;
import net.advanceteam.proxy.common.chat.component.TextComponent;
import net.advanceteam.proxy.common.event.ProxyEvent;
import net.advanceteam.proxy.common.event.cancel.Cancellable;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.connection.server.impl.Server;

@Getter
@Setter
public class ServerKickEvent extends ProxyEvent implements Cancellable {


    private boolean cancelled;

    private final Player player;

    private final Server kickedFrom;

    private BaseComponent[] kickReasonComponent;

    private Server cancelServer;

    private State state;


    public ServerKickEvent(Player player, BaseComponent[] kickReasonComponent, Server cancelServer) {
        this(player, kickReasonComponent, cancelServer, State.UNKNOWN);
    }

    public ServerKickEvent(Player player, BaseComponent[] kickReasonComponent, Server cancelServer, State state) {
        this(player, player.getServer(), kickReasonComponent, cancelServer, state);
    }

    public ServerKickEvent(Player player, Server kickedFrom, BaseComponent[] kickReasonComponent, Server cancelServer, State state) {
        this.player = player;
        this.kickedFrom = kickedFrom;
        this.kickReasonComponent = kickReasonComponent;
        this.cancelServer = cancelServer;
        this.state = state;
    }

    public String getKickReason() {
        return BaseComponent.toLegacyText(kickReasonComponent);
    }

    public void setKickReason(String reason) {
        kickReasonComponent = TextComponent.fromLegacyText(reason);
    }

    public enum State {

        CONNECTING, CONNECTED, UNKNOWN
    }
}
