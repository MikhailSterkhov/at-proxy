package net.advanceteam.proxy.common.event.manager;

import lombok.Getter;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.event.ProxyEvent;
import net.advanceteam.proxy.common.event.Listener;
import net.advanceteam.proxy.common.event.annotation.EventHandler;
import net.advanceteam.proxy.common.plugin.ProxyPlugin;
import net.advanceteam.proxy.common.plugin.manager.PluginManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public final class EventManager {

    private final Map<String, List<Method>> registeredEventMethods = new HashMap<>();
    private final Map<String, List<Listener>> registeredListeners = new HashMap<>();


    /**
     * Зарегистрировать листенер
     *
     * @param listener - листенер
     */
    public void registerListener(ProxyPlugin proxyPlugin, Listener listener) {
        String pluginName = proxyPlugin == null ? "AdvanceProxy" : proxyPlugin.getPluginInfo().name();

        List<Listener> listenerList = registeredListeners.computeIfAbsent(pluginName, f -> new ArrayList<>());
        listenerList.add(listener);

        registeredListeners.put(pluginName, listenerList);

        registerEvents(listener);
    }

    /**
     * Разрегистрировать листенеры
     *
     * @param proxyPlugin - плагин, от имени которого зарегистрированы листенеры
     */
    public void unregisterListeners(ProxyPlugin proxyPlugin) {
        String pluginName = proxyPlugin == null ? "AdvanceProxy" : proxyPlugin.getPluginInfo().name();

        registeredListeners.remove(pluginName);
    }

    /**
     * Зарегистрировать ивенты из листенера
     *
     * @param listener - листенер с ивентами
     */
    private void registerEvents(Listener listener) {
        Method[] methodArray = listener.getClass().getMethods();

        for (Method method : methodArray) {
            if (method.getDeclaredAnnotation(EventHandler.class) == null) return;

            Class<?>[] parameters = method.getParameterTypes();

            if (parameters.length != 1) return;

            Class<?> eventClass = parameters[0];

            if (!eventClass.getSuperclass().equals(ProxyEvent.class)) return;

            List<Method> methodList = registeredEventMethods
                    .getOrDefault(eventClass.getSimpleName(), new ArrayList<>());

            methodList.add(method);

            registeredEventMethods.put(eventClass.getSimpleName(), methodList);
        }
    }

    /**
     * Вызывать ивент из всех зарегистрированных
     * листенеров
     *
     * @param proxyEvent - ивент
     */
    public void callEvent(ProxyEvent proxyEvent) {
        List<Method> methodList = registeredEventMethods.get(proxyEvent.getClass().getSimpleName());

        proxyEvent.postCall();

        if (methodList == null) {
            return;
        }

        for (Method method : methodList) {
            registeredListeners.forEach((pluginName, listenerList) -> {
                PluginManager pluginManager = AdvanceProxy.getInstance().getPluginManager();
                ProxyPlugin proxyPlugin = pluginManager.getPlugin(pluginName);

                if (proxyPlugin != null && !proxyPlugin.isEnabled()) {
                    return;
                }

                for (Listener listener : listenerList) {
                    try {
                        method.invoke(listener, proxyEvent);
                    } catch (IllegalAccessException | InvocationTargetException ignored) {
                    }
                }
            });
        }
    }

}
