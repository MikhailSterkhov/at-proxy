package net.advanceteam.proxy.common.event.impl;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.advanceteam.proxy.common.event.ProxyEvent;
import net.advanceteam.proxy.common.event.cancel.Cancellable;

@Getter
@RequiredArgsConstructor
public class ProxyFullKickEvent extends ProxyEvent implements Cancellable {

    private boolean cancelled;

    private final String playerName;
    private final Channel channel;

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
