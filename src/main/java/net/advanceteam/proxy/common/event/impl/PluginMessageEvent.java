package net.advanceteam.proxy.common.event.impl;

import lombok.*;
import net.advanceteam.proxy.common.event.ProxyEvent;
import net.advanceteam.proxy.common.event.cancel.Cancellable;
import net.advanceteam.proxy.connection.player.Player;

@RequiredArgsConstructor
@Getter
@Setter
public class PluginMessageEvent extends ProxyEvent implements Cancellable {

    private boolean cancelled;

    private final Player player, receiver;

    private final String tag;
    private final byte[] data;

}
