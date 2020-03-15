package net.advanceteam.proxy.common.event.cancel;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancel);
}
