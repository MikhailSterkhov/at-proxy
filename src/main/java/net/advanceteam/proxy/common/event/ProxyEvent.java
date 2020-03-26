package net.advanceteam.proxy.common.event;

public abstract class ProxyEvent {

    public String getName() {
        return getClass().getSimpleName();
    }

    public void postCall() { }

}
