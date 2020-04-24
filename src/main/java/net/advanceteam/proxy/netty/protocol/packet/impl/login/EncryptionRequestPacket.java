package net.advanceteam.proxy.netty.protocol.packet.impl.login;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.advanceteam.proxy.netty.buffer.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EncryptionRequestPacket implements MinecraftPacket {

    private String serverId;
    private byte[] publicKey;
    private byte[] verifyToken;

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
