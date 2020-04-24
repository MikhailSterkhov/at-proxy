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
public class SetCompressionPacket implements MinecraftPacket {

    private int threshold;

    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        channelPacketBuffer.writeVarInt(threshold);
    }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        this.threshold = channelPacketBuffer.readVarInt();
    }

    @Override
    public void handle(Channel channel) { }

}
