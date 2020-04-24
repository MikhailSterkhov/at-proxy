package net.advanceteam.proxy.netty.protocol.packet.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.advanceteam.proxy.netty.buffer.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;

@Data
@AllArgsConstructor
public class UndefinedPacket implements MinecraftPacket {

    private int packetId;
    private ByteBuf buf;

    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        channelPacketBuffer.writeBytes(buf);
    }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        buf = channelPacketBuffer.slice(channelPacketBuffer.readerIndex(), channelPacketBuffer.readableBytes()).retain();
    }

    @Override
    public void handle(Channel channel) { }

}
