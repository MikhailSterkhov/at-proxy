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
public class DisconnectPacket implements MinecraftPacket {

    private String reason;

    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        channelPacketBuffer.writeString(reason);
    }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) { }

    @Override
    public void handle(Channel channel) { }

}
