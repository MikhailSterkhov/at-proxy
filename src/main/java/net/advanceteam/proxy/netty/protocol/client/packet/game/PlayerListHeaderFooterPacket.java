package net.advanceteam.proxy.netty.protocol.client.packet.game;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

@AllArgsConstructor
public class PlayerListHeaderFooterPacket implements ClientPacket {

    private String header;
    private String footer;

    public PlayerListHeaderFooterPacket() {
        registerClientPacket(ClientVersion.V1_8, 0x47);
        registerClientPacket(ClientVersion.V1_9, 0x48);
        registerClientPacket(ClientVersion.V1_9_1, 0x48);
        registerClientPacket(ClientVersion.V1_9_2, 0x48);
        registerClientPacket(ClientVersion.V1_9_3, 0x48);
        registerClientPacket(ClientVersion.V1_9_4, 0x47);
        registerClientPacket(ClientVersion.V1_10, 0x47);
        registerClientPacket(ClientVersion.V1_11, 0x47);
        registerClientPacket(ClientVersion.V1_11_1, 0x47);
        registerClientPacket(ClientVersion.V1_11_2, 0x47);
        registerClientPacket(ClientVersion.V1_12, 0x49);
        registerClientPacket(ClientVersion.V1_12_1, 0x4A);
        registerClientPacket(ClientVersion.V1_12_2, 0x4A);
        registerClientPacket(ClientVersion.V1_13, 0x4E);
        registerClientPacket(ClientVersion.V1_13_1, 0x4E);
        registerClientPacket(ClientVersion.V1_13_2, 0x4E);
        registerClientPacket(ClientVersion.V1_14, 0x53);
        registerClientPacket(ClientVersion.V1_14_1, 0x53);
        registerClientPacket(ClientVersion.V1_14_2, 0x53);
        registerClientPacket(ClientVersion.V1_14_3, 0x53);
        registerClientPacket(ClientVersion.V1_14_4, 0x53);
        registerClientPacket(ClientVersion.V1_15, 0x54);
        registerClientPacket(ClientVersion.V1_15_1, 0x54);
    }

    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        channelPacketBuffer.writeString(header);
        channelPacketBuffer.writeString(footer);
    }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        this.header = channelPacketBuffer.readString();
        this.footer = channelPacketBuffer.readString();
    }

    @Override
    public void handle(Channel channel) { }
}
