package net.advanceteam.proxy.netty.protocol.client;

import io.netty.channel.Channel;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

public interface ClientPacket {


    void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion);

    void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion);

    void handle(Channel channel);


    default void registerClientPacket(ClientVersion clientVersion, int packetId) {
        AdvanceProxy.getInstance().getClientPacketManager().registerPacket(getClass(), clientVersion.getVersion(), packetId);
    }
}
