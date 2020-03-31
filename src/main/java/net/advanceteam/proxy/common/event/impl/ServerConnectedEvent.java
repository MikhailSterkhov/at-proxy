package net.advanceteam.proxy.common.event.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.advanceteam.proxy.common.event.ProxyEvent;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.connection.server.Server;

@RequiredArgsConstructor
@Getter
public class ServerConnectedEvent extends ProxyEvent {

    private final Player player;
    private final Server server;
}
