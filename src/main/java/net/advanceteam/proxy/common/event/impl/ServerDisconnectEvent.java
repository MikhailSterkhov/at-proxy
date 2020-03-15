package net.advanceteam.proxy.common.event.impl;

import lombok.*;
import net.advanceteam.proxy.common.event.ProxyEvent;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.connection.server.impl.Server;

@RequiredArgsConstructor
@Getter
public class ServerDisconnectEvent extends ProxyEvent {

    private final Player player;
    private final Server target;
}
