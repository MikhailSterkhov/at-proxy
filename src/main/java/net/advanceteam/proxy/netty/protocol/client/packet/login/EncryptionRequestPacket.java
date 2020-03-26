package net.advanceteam.proxy.netty.protocol.client.packet.login;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

@Getter
@AllArgsConstructor
public class EncryptionRequestPacket implements ClientPacket {

    private String serverId;
    private byte[] publicKey;
    private byte[] verifyToken;

    public EncryptionRequestPacket() {
        for (ClientVersion clientVersion : ClientVersion.values()) {
            registerClientPacket(clientVersion, 0x01);
        }
    }

    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        channelPacketBuffer.writeString(serverId);
        channelPacketBuffer.writeArray(publicKey);
        channelPacketBuffer.writeArray(verifyToken);
    }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        this.serverId = channelPacketBuffer.readString();
        this.publicKey = channelPacketBuffer.readArray();
        this.verifyToken = channelPacketBuffer.readArray();
    }

    @Override
    public void handle(Channel channel) { }

}
