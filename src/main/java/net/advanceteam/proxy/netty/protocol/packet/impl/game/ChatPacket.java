package net.advanceteam.proxy.netty.protocol.packet.impl.game;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.advanceteam.proxy.netty.buffer.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatPacket implements MinecraftPacket {

    private String message;
    private byte position;

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
