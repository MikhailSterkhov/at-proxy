package net.advanceteam.proxy.common.event.impl;

import lombok.*;
import net.advanceteam.proxy.common.event.ProxyEvent;
import net.advanceteam.proxy.connection.player.Player;


@RequiredArgsConstructor
@Getter
public class PlayerDisconnectEvent extends ProxyEvent {

    private final Player player;
}
