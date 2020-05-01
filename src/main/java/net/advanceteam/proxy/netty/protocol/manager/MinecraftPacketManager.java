package net.advanceteam.proxy.netty.protocol.manager;

import gnu.trove.map.hash.TIntObjectHashMap;
import io.netty.channel.Channel;
import net.advanceteam.proxy.netty.protocol.codec.MinecraftPacketDecoder;
import net.advanceteam.proxy.netty.protocol.direction.ProtocolDirection;
import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;
import net.advanceteam.proxy.netty.protocol.packet.info.MinecraftPacketInfo;
import net.advanceteam.proxy.netty.protocol.status.ProtocolStatus;
import net.advanceteam.proxy.netty.protocol.storage.MinecraftPacketVersionStorage;
import net.advanceteam.proxy.netty.protocol.version.MinecraftVersion;

public final class MinecraftPacketManager {

    private final TIntObjectHashMap<MinecraftPacketVersionStorage> packetToClientMap = new TIntObjectHashMap<>();
    private final TIntObjectHashMap<MinecraftPacketVersionStorage> packetToServerMap = new TIntObjectHashMap<>();

    public final int MAX_PACKET_ID = 0xFF;


    public MinecraftPacketManager() {
        for (MinecraftVersion clientVersion : MinecraftVersion.values()) {
            packetToClientMap.put(clientVersion.getVersionId(), new MinecraftPacketVersionStorage());
            packetToServerMap.put(clientVersion.getVersionId(), new MinecraftPacketVersionStorage());
        }
    }


    public void setProtocolStatus(Channel channel, ProtocolStatus protocolType) {
        channel.pipeline().get(MinecraftPacketDecoder.class).setProtocolStatus(protocolType);
    }

    public ProtocolStatus getProtocolStatus(Channel channel) {
        return channel.pipeline().get(MinecraftPacketDecoder.class).getProtocolStatus();
    }

    public void registerPacket(ProtocolStatus protocolType,
                               ProtocolDirection protocolDirection,

                               MinecraftPacket minecraftPacket,

                               int clientVersion,
                               int packetId) {

        MinecraftPacketVersionStorage clientPacketVersionStorage = getPacketStorage(protocolDirection, clientVersion);

        MinecraftPacketInfo packetInfo = new MinecraftPacketInfo(packetId,
                minecraftPacket.getClass(),
                minecraftPacket,
                protocolType);

        clientPacketVersionStorage.registerPacket(packetInfo, packetId);
    }

    private MinecraftPacketVersionStorage getPacketStorage(ProtocolDirection protocolDirection, int clientVersion) {
        MinecraftPacketVersionStorage clientPacketVersionStorage
                = protocolDirection == ProtocolDirection.TO_SERVER ? packetToServerMap.get(clientVersion) : packetToClientMap.get(clientVersion);

        if (clientPacketVersionStorage == null) {
            throw new NullPointerException("Can't find client packet version storage by version " + clientVersion);
        }

        return clientPacketVersionStorage;
    }

    public MinecraftPacket getNewPacket(ProtocolStatus protocolType, int versionId, int packetId) {
        if (packetId > MAX_PACKET_ID) {
            return null;
        }

        return getPacketStorage(ProtocolDirection.TO_SERVER, versionId).getPacket(protocolType, packetId);
    }

    public boolean packetIsExists(int packetId) {
        return packetToClientMap.forEachEntry((clientVersion, packetStorage) -> packetStorage.packetIsExists(packetId));
    }

    public int getPacketId(Class<? extends MinecraftPacket> clientPacketClass, int clientVersion) {
        MinecraftPacketVersionStorage clientPacketVersionStorage = getPacketStorage(ProtocolDirection.TO_CLIENT, clientVersion);
        int packetId = clientPacketVersionStorage.getPacketId(clientPacketClass);

        if (packetId < 0) {
            clientPacketVersionStorage = getPacketStorage(ProtocolDirection.TO_SERVER, clientVersion);
            packetId = clientPacketVersionStorage.getPacketId(clientPacketClass);
        }

        return packetId;
    }
}
