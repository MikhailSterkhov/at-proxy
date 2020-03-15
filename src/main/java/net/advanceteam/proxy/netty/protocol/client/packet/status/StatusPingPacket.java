package net.advanceteam.proxy.netty.protocol.client.packet.status;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.annotation.ClientPacketHandler;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

@AllArgsConstructor
@ClientPacketHandler(packetQuery = "STATUS_PING_PACKET")
public class StatusPingPacket implements ClientPacket {

    private long responseTime;

    public StatusPingPacket() {
        for (ClientVersion clientVersion : ClientVersion.values()) {
            registerClientPacket(clientVersion, 0x01);
        }
    }

    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        channelPacketBuffer.writeLong(responseTime);
    }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        responseTime = channelPacketBuffer.readLong();
    }

    @Override
    public void handle(Channel channel) { }
}
