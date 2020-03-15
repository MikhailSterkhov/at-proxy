package net.advanceteam.proxy.netty.protocol.client.manager;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.storage.ClientPacketVersionStorage;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

public class ClientPacketManager {

    private final TIntObjectHashMap<ClientPacketVersionStorage> packetsByClientVersionMap = new TIntObjectHashMap<>();

    public ClientPacketManager() {
        for (ClientVersion clientVersion : ClientVersion.values()) {
            packetsByClientVersionMap.put(clientVersion.getVersion(), new ClientPacketVersionStorage());
        }
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
