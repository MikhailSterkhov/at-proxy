package net.advanceteam.proxy.netty.protocol.client.storage;

import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.annotation.ClientPacketHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientPacketVersionStorage {

    private final Map<Integer, List<Class<? extends ClientPacket>>> clientPacketMap = new HashMap<>();
    private final Map<Class<? extends ClientPacket>, Integer> packetIdsMap = new HashMap<>();


    /**
     * Регистрация пакетов.
     *
     * @param clientPacketClass - класс с пакетомю
     * @param packetId          - ид пакета.
     */
    public void registerPacket(Class<? extends ClientPacket> clientPacketClass, int packetId) {
        clientPacketMap.computeIfAbsent(packetId, packet -> new ArrayList<>()).add(clientPacketClass);
        packetIdsMap.put(clientPacketClass, packetId);
    }

    /**
     * Создание нового пакета.
     *
     * @param packetId - ид пакета.
     * @return - новый класс с пакетом.
     */
    public ClientPacket createNewPacket(String packetType, int packetId) {
        if (clientPacketMap.get(packetId).size() == 0) {
            throw new NullPointerException("Packet size by packetId is 0");
        }

        for (Class<? extends ClientPacket> clientPacketClass : clientPacketMap.get(packetId)) {
            ClientPacketHandler clientPacketHandler = clientPacketClass.getDeclaredAnnotation(ClientPacketHandler.class);

            if (clientPacketHandler == null) {
                throw new NullPointerException("Packet " + clientPacketClass.getSimpleName() + " not annotated");
            }

            try {

                if (clientPacketHandler.packetQuery().equalsIgnoreCase(packetType)) {
                    return clientPacketClass.newInstance();
                }

            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        throw new NullPointerException("Can't find packet by packetId: " + packetId);
    }

    /**
     * Получить ID пакета
     *
     * @param packetClass - класс пакета
     */
    public int getPacketId(Class<? extends ClientPacket> packetClass) {
        return packetIdsMap.getOrDefault(packetClass, -1);
    }

    /**
     * Проверка существует ли пакет.
     *
     * @param packetId - ид пакета.
     * @return - true или false/
     */
    public boolean packetIsExists(int packetId) {
        return clientPacketMap.get(packetId) != null;
    }
}
