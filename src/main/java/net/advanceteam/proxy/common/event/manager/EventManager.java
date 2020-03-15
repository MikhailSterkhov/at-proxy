package net.advanceteam.proxy.common.event.manager;

import lombok.Getter;
import net.advanceteam.proxy.common.event.ProxyEvent;
import net.advanceteam.proxy.common.event.Listener;
import net.advanceteam.proxy.common.event.annotation.EventHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public final class EventManager {

    private final Map<String, List<Method>> registeredEventMethods = new HashMap<>();
    private final List<Listener> registeredListeners = new ArrayList<>();


    /**
     * Зарегистрировать листенер
     *
     * @param listener - листенер
     */
    public void registerListener(Listener listener) {
        registeredListeners.add(listener);

        registerEvents(listener);
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
            for (Listener listener : registeredListeners) {
                try {
                    method.invoke(listener, proxyEvent);
                   } catch (IllegalAccessException | InvocationTargetException ignored) {
                }
            }
        }
    }

}
