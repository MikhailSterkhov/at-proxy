package net.advanceteam.proxy.netty.protocol.client.manager;

import gnu.trove.map.hash.TIntObjectHashMap;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.codec.ClientPacketDecoder;
import net.advanceteam.proxy.netty.protocol.client.codec.ClientPacketEncoder;
import net.advanceteam.proxy.netty.protocol.client.storage.ClientPacketVersionStorage;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

public final class ClientPacketManager {

    private final TIntObjectHashMap<ClientPacketVersionStorage> packetsByClientVersionMap = new TIntObjectHashMap<>();


    public ClientPacketManager() {
        for (ClientVersion clientVersion : ClientVersion.values()) {
            packetsByClientVersionMap.put(clientVersion.getVersion(), new ClientPacketVersionStorage());
        }
    }


    public void setPacketType(Channel channel, String packetType) {
        channel.pipeline().get(ClientPacketDecoder.class).setPacketType(packetType);
        channel.pipeline().get(ClientPacketEncoder.class).setPacketType(packetType);
    }

    public String getPacketType(Channel channel) {
        return channel.pipeline().get(ClientPacketDecoder.class).getPacketType();
    }

    public void registerPacket(Class<? extends ClientPacket> clientPacketClass, int clientVersion, int packetId) {
        ClientPacketVersionStorage clientPacketVersionStorage = getPacketStorage(clientVersion);

        clientPacketVersionStorage.registerPacket(clientPacketClass, packetId);
    }

    private ClientPacketVersionStorage getPacketStorage(int clientVersion) {
        ClientPacketVersionStorage clientPacketVersionStorage = packetsByClientVersionMap.get(clientVersion);

        if (clientPacketVersionStorage == null) {
            throw new NullPointerException("Can't find client packet version storage by version " + clientVersion);
        }

        return clientPacketVersionStorage;

    }

    public ClientPacket getNewPacket(String packetType, int clientVersion, int packetId) {
        return getPacketStorage(clientVersion).createNewPacket(packetType, packetId);
    }

    public boolean packetIsExists(int packetId) {
        return packetsByClientVersionMap.forEachEntry((clientVersion, packetStorage) -> packetStorage.packetIsExists(packetId));
    }

    public int getPacketId(Class<? extends ClientPacket> clientPacketClass, int clientVersion) {
        ClientPacketVersionStorage clientPacketVersionStorage = getPacketStorage(clientVersion);

        return clientPacketVersionStorage.getPacketId(clientPacketClass);
    }
}
