package net.advanceteam.proxy.connection.manager;

import lombok.Getter;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.connection.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerManager {

    @Getter
    private final Map<String, Player> playerMap = new HashMap<>();

    @Getter
    private final Map<UUID, Player> uuidPlayerMap = new HashMap<>();

    /**
     * Добавить игрока
     *
     * @param playerName - ник игрока
     * @param player - соединение игрока
     */
    private void addPlayer(String playerName, Player player) {
        playerMap.put(playerName.toLowerCase(), player);
        uuidPlayerMap.put(player.getUniqueId(), player);
    }

    /**
     * Удалить игрока
     *
     * @param playerName - ник игрока
     */
    private void removePlayer(String playerName) {
        Player player = playerMap.remove(playerName.toLowerCase());

        uuidPlayerMap.remove(player.getUniqueId());
    }

    /**
     * Получить соединение игрока по его нику
     *
     * @param playerName - ник игрока
     */
    public Player getPlayer(String playerName) {
        return playerMap.get(playerName.toLowerCase());
    }

    /**
     * Получить соединение игрока по его UUID
     *
     * @param uniqueId - UUID игрока
     */
    public Player getPlayer(UUID uniqueId) {
        return uuidPlayerMap.get(uniqueId);
    }

    /**
     * Вернуть булевое выражение, говорящее о том,
     * существует ли соединение игрока
     *
     * @param playerName - имя игрока
     */
    public boolean playerIsConnected(String playerName) {
        return playerMap.containsKey(playerName.toLowerCase());
    }


    /**
     * Подключить игрока к серверу
     *
     * @param player - игрока
     */
    public void connectPlayer(Player player) {
        AdvanceProxy.getInstance().getLogger().info(String.format("[Player] -> Player %s has connected to Proxy", player.getName()));

        this.addPlayer(player.getName(), player);
    }

    /**
     * Отключить игрока от сервера
     *
     * @param playerName - ник игрока
     */
    public void disconnectPlayer(String playerName) {
        AdvanceProxy.getInstance().getLogger().info(
                String.format("[Player] -> Player %s has disconnected from Proxy", playerName));

        this.removePlayer(playerName);
    }
}
