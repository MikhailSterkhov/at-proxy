package net.advanceteam.proxy.common.event;

import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.plugin.ProxyPlugin;

public interface Listener {

    /**
     * Зарегистрировать данный листенер от имени
     * указанного плагина
     *
     * @param proxyPlugin - плагин
     */
    default void register(ProxyPlugin proxyPlugin) {
        AdvanceProxy.getInstance().getEventManager().registerListener(proxyPlugin, this);
    }

}
