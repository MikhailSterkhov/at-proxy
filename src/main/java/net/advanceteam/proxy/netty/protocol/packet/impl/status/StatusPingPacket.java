package net.advanceteam.proxy.netty.protocol.packet.impl.status;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.advanceteam.proxy.netty.buffer.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusPingPacket implements MinecraftPacket {

    private long responseTime;

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
