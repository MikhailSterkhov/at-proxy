package net.advanceteam.proxy.netty.protocol.packet;

import io.netty.channel.Channel;
import net.advanceteam.proxy.netty.buffer.ChannelPacketBuffer;

public interface MinecraftPacket {

    void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion);

    void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion);

    void handle(Channel channel);
}
