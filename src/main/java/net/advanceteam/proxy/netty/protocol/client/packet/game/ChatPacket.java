package net.advanceteam.proxy.netty.protocol.client.packet.game;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.annotation.ClientPacketHandler;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

@AllArgsConstructor
@ClientPacketHandler(packetQuery = "GAME")
public class ChatPacket implements ClientPacket {

    private String message;
    private byte position;

    public ChatPacket() {
        registerClientPacket(ClientVersion.V1_8, 0x02);
        registerClientPacket(ClientVersion.V1_9, 0x0F);
        registerClientPacket(ClientVersion.V1_9_1, 0x0F);
        registerClientPacket(ClientVersion.V1_9_2, 0x0F);
        registerClientPacket(ClientVersion.V1_9_3, 0x0F);
        registerClientPacket(ClientVersion.V1_9_4, 0x0F);
        registerClientPacket(ClientVersion.V1_10, 0x0F);
        registerClientPacket(ClientVersion.V1_11, 0x0F);
        registerClientPacket(ClientVersion.V1_11_1, 0x0F);
        registerClientPacket(ClientVersion.V1_11_2, 0x0F);
        registerClientPacket(ClientVersion.V1_12, 0x0F);
        registerClientPacket(ClientVersion.V1_12_1, 0x0F);
        registerClientPacket(ClientVersion.V1_12_2, 0x0F);
        registerClientPacket(ClientVersion.V1_13, 0x0E);
        registerClientPacket(ClientVersion.V1_13_1, 0x0E);
        registerClientPacket(ClientVersion.V1_13_2, 0x0E);
        registerClientPacket(ClientVersion.V1_14, 0x0E);
        registerClientPacket(ClientVersion.V1_14_1, 0x0E);
        registerClientPacket(ClientVersion.V1_14_2, 0x0E);
        registerClientPacket(ClientVersion.V1_14_3, 0x0E);
        registerClientPacket(ClientVersion.V1_14_4, 0x0E);
        registerClientPacket(ClientVersion.V1_15, 0x0F);
        registerClientPacket(ClientVersion.V1_15_1, 0x0F);
        registerClientPacket(ClientVersion.V1_15_2, 0x0F);
    }

    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        channelPacketBuffer.writeString(message);
        channelPacketBuffer.writeByte(position);
    }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        message = channelPacketBuffer.readString();
        position = channelPacketBuffer.readByte();
    }

    @Override
    public void handle(Channel channel) { }

}
