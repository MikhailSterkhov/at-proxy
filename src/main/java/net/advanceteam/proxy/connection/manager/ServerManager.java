package net.advanceteam.proxy.connection.manager;

import lombok.Getter;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.connection.server.impl.Server;

import java.util.HashMap;
import java.util.Map;

public class ServerManager {

    @Getter
    private final Map<String, Server> serverMap = new HashMap<>();

    /**
     * Добавить серер
     *
     * @param server - имя сервера
     */
    public void addServer(Server server) {
        serverMap.put(server.getName().toLowerCase(), server);
    }

    /**
     * Удалить сервер
     *
     * @param server - имя сервера
     */
    public void removeServer(String server) {
        serverMap.remove(server.toLowerCase());
    }

    /**
     * Получить сервер по его имени
     *
     * @param server - имя сервера
     */
    public Server getServer(String server) {
        return serverMap.get(server.toLowerCase());
    }

    /**
     * Покдлючить сервер к прокси
     *
     * @param server - сервер
     */
    public void connectServer(Server server) {
        AdvanceProxy.getInstance().getLogger().info(
                String.format("[Bukkit] -> %s has been connected to Proxy", server.getName()));

        this.addServer(server);
    }

    /**
     * Отключить сервер от прокси
     *
     * @param serverName - имя сервера
     */
    public void disconnectServer(String serverName) {
        AdvanceProxy.getInstance().getLogger().info(
                String.format("[Bukkit] -> %s has been disconnected from Proxy", serverName));

        this.removeServer(serverName);
    }
}
