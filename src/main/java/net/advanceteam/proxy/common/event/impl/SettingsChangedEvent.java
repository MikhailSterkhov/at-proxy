package net.advanceteam.proxy.common.event.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.advanceteam.proxy.common.event.ProxyEvent;
import net.advanceteam.proxy.connection.player.Player;

@RequiredArgsConstructor
@Getter
public class SettingsChangedEvent extends ProxyEvent {

    private final Player player;
}
