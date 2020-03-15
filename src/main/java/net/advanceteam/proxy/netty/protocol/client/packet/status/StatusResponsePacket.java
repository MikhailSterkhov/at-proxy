package net.advanceteam.proxy.netty.protocol.client.packet.status;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.annotation.ClientPacketHandler;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

@AllArgsConstructor
@ClientPacketHandler
public class StatusResponsePacket implements ClientPacket {

    private String information;

    public StatusResponsePacket() {
        for (ClientVersion clientVersion : ClientVersion.values()) {
            registerClientPacket(clientVersion, 0x00);
        }
    }

    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        channelPacketBuffer.writeString(information);
    }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        this.information = channelPacketBuffer.readString();
    }

    @Override
    public void handle(Channel channel) { }
}
