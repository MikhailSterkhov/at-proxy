package net.advanceteam.proxy.netty.protocol.client.packet.game;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.annotation.ClientPacketHandler;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

@AllArgsConstructor
@ClientPacketHandler(packetQuery = "GAME")
public class DisconnectPacket implements ClientPacket {

    private String reason;

    public DisconnectPacket() {
        for (ClientVersion clientVersion : ClientVersion.values()) {
            registerClientPacket(clientVersion, 0x00);
        }
    }

    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        channelPacketBuffer.writeString(reason);
    }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        this.reason = channelPacketBuffer.readString();
    }

    @Override
    public void handle(Channel channel) {
        System.out.println("disconnect packet handle: " + reason);
    }

}
