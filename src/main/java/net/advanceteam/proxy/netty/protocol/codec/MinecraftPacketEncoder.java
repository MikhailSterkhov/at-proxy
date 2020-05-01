package net.advanceteam.proxy.netty.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Getter;
import lombok.Setter;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.netty.buffer.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;
import net.advanceteam.proxy.netty.protocol.manager.MinecraftPacketManager;
import net.advanceteam.proxy.netty.protocol.packet.impl.UndefinedPacket;
import net.advanceteam.proxy.netty.protocol.packet.impl.handshake.HandshakePacket;
import net.advanceteam.proxy.netty.protocol.version.MinecraftVersion;

public class MinecraftPacketEncoder extends MessageToByteEncoder<MinecraftPacket> {

    @Setter
    @Getter
    private MinecraftVersion minecraftVersion = MinecraftVersion.V1_15_2;


    /**
     * Отправка пакета по каналу.
     *
     * @param channelHandlerContext - канал.
     * @param minecraftPacket - пакет.
     * @param byteBuf - буфер обмена.
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MinecraftPacket minecraftPacket, ByteBuf byteBuf) {
        ChannelPacketBuffer channelPacketBuffer = new ChannelPacketBuffer(byteBuf);
        MinecraftPacketManager packetManager = AdvanceProxy.getInstance().getMinecraftPacketManager();

        int packetId;

        if (minecraftPacket instanceof UndefinedPacket) {
            packetId = ((UndefinedPacket) minecraftPacket).getPacketId();
        } else {
            packetId = packetManager.getPacketId(minecraftPacket.getClass(), minecraftVersion.getVersionId());
        }

        if (packetId > packetManager.MAX_PACKET_ID) {
            return;
        }

        if (AdvanceProxy.getInstance().getProxyConfig().getProxySettings().isLogWritePacket()) {
            AdvanceProxy.getInstance().getLogger().info("Write packet @" + minecraftPacket.getClass().getSimpleName() + "(" + packetId + ")");
        }

        int clientVersionId = minecraftVersion.getVersionId();

        if (minecraftPacket instanceof HandshakePacket) {
            clientVersionId = ((HandshakePacket) minecraftPacket).getClientVersion();

            this.minecraftVersion = MinecraftVersion.getByVersionId(clientVersionId);
        }

        channelPacketBuffer.writeVarInt(packetId);
        minecraftPacket.writePacket(channelPacketBuffer, clientVersionId);
    }
}
