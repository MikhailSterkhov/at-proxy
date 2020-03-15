package net.advanceteam.proxy.common.event;

public abstract class ProxyEvent {

    /**
     * Получить имя ивента
     */
    public String getName() {
        return getClass().getSimpleName();
    }

    public void postCall() { }

}
