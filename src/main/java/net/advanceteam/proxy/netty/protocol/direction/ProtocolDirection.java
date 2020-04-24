package net.advanceteam.proxy.netty.protocol.direction;

import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;
import net.advanceteam.proxy.netty.protocol.status.ProtocolStatus;
import net.advanceteam.proxy.netty.protocol.version.MinecraftVersion;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

public enum ProtocolDirection {

    /**
     * Напраление пакетов со стороны сервера
     * к клиенту игрока (read)
     */
    TO_SERVER,

    /**
     * Направление пакетов со стороны клиента
     * к серверу (write)
     */
    TO_CLIENT;



    /**
     * Регистрация пакета, используя разные версии клиента
     *
     * @param protocolType - тип пакета
     * @param minecraftVersion - версия клиента
     * @param packetInstance - пакет
     * @param packetId - номер пакета
     */
    private void registerPacket(ProtocolStatus protocolType,
                                MinecraftVersion minecraftVersion,

                                Supplier<MinecraftPacket> packetInstance,

                                int packetId) {

        AdvanceProxy.getInstance().getMinecraftPacketManager().registerPacket(protocolType, this,
                packetInstance.get(), minecraftVersion.getVersionId(), packetId);
    }

    /**
     * Регистрация пакета, используя все версии клиента
     *
     * @param protocolType - тип пакета
     * @param packetInstance - пакет
     * @param packetId - номер пакета
     */
    public void registerPacket(ProtocolStatus protocolType,
                               Supplier<MinecraftPacket> packetInstance,

                               int packetId) {

        Arrays.asList(MinecraftVersion.values()).forEach(
                minecraftVersion -> registerPacket(protocolType, minecraftVersion, packetInstance, packetId));
    }

    /**
     * Регистрация пакета, используя указанные версии клиента
     *
     * @param protocolType - тип пакета
     * @param packetInstance - пакет
     * @param packetVersionMap - список версий с номерами пакетов
     */
    public void registerPacket(ProtocolStatus protocolType,
                               Supplier<MinecraftPacket> packetInstance,

                               Map<MinecraftVersion, Integer> packetVersionMap) {

        packetVersionMap.forEach((minecraftVersion, packetId)
                -> registerPacket(protocolType, minecraftVersion, packetInstance, packetId));
    }
}
