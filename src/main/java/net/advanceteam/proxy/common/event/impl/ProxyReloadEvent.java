package net.advanceteam.proxy.common.event.impl;

import lombok.Getter;
import net.advanceteam.proxy.common.event.ProxyEvent;
import net.advanceteam.proxy.common.event.cancel.Cancellable;

@Getter
public class ProxyReloadEvent extends ProxyEvent implements Cancellable {

    private boolean cancelled = false;

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
