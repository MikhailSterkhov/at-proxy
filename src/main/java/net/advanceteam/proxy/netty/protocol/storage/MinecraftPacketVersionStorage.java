package net.advanceteam.proxy.netty.protocol.storage;

import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;
import net.advanceteam.proxy.netty.protocol.packet.info.MinecraftPacketInfo;
import net.advanceteam.proxy.netty.protocol.status.ProtocolStatus;

import java.util.HashMap;
import java.util.Map;

public class MinecraftPacketVersionStorage {

    private final Map<MinecraftPacketInfo, Integer> packetMap = new HashMap<>();


    /**
     * Регистрация пакетов.
     *
     * @param packetInfo   - информация о пакет
     * @param packetId     - ид пакета.
     */
    public void registerPacket(MinecraftPacketInfo packetInfo, int packetId) {
        packetMap.put(packetInfo, packetId);
    }

    /**
     * Получить зарегистрированный пакет по его номеру
     *
     * @param protocolType  - тип пакета
     * @param packetId      - ид пакета.
     */
    public MinecraftPacket getPacket(ProtocolStatus protocolType, int packetId) {
        return packetMap.keySet().stream()

                .filter(packetInfo -> packetInfo.getProtocolStatus().equals(protocolType) && packetInfo.getPacketId() == packetId)
                .findFirst()
                .orElse(null)

                .getPacketInstance();
    }

    /**
     * Получить зарегистрированный пакет по его номеру
     *
     * @param protocolType  - тип пакета
     * @param packetClass   - класс пакета.
     */
    public <T extends MinecraftPacket> T getPacket(ProtocolStatus protocolType, Class<T> packetClass) {
        return (T) packetMap.keySet().stream()

                .filter(packetInfo -> packetInfo.getProtocolStatus().equals(protocolType) && packetInfo.getPacketClass().isAssignableFrom(packetClass))
                .findFirst()
                .orElse(null)

                .getPacketInstance();
    }

    /**
     * Получить ID пакета
     *
     * @param packetClass - класс пакета
     */
    public int getPacketId(Class<? extends MinecraftPacket> packetClass) {
        return packetMap.keySet().stream()

                .filter(packetInfo -> packetInfo.getPacketClass().isAssignableFrom(packetClass))
                .findFirst()
                .orElse(null)

                .getPacketId();
    }

    /**
     * Проверка существует ли пакет.
     *
     * @param packetId - номер пакета.
     */
    public boolean packetIsExists(int packetId) {
        return packetMap.containsValue(packetId);
    }
}
