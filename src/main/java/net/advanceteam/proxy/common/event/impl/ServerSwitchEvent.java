package net.advanceteam.proxy.common.event.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.advanceteam.proxy.common.event.ProxyEvent;
import net.advanceteam.proxy.connection.player.Player;

@Getter
@RequiredArgsConstructor
public class ServerSwitchEvent extends ProxyEvent {

    private final Player player;
}
