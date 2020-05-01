package net.advanceteam.proxy.netty.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.Getter;
import lombok.Setter;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.netty.buffer.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.manager.MinecraftPacketManager;
import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;
import net.advanceteam.proxy.netty.protocol.packet.impl.UndefinedPacket;
import net.advanceteam.proxy.netty.protocol.packet.impl.handshake.HandshakePacket;
import net.advanceteam.proxy.netty.protocol.packet.impl.login.EncryptionRequestPacket;
import net.advanceteam.proxy.netty.protocol.packet.impl.login.LoginRequestPacket;
import net.advanceteam.proxy.netty.protocol.status.ProtocolStatus;
import net.advanceteam.proxy.netty.protocol.version.MinecraftVersion;

import java.util.List;

public class MinecraftPacketDecoder extends ByteToMessageDecoder {

    @Setter
    @Getter
    private MinecraftVersion minecraftVersion = MinecraftVersion.V1_15_2;

    @Setter
    @Getter
    private ProtocolStatus protocolStatus = ProtocolStatus.HANDSHAKE;


    @Setter
    @Getter
    private String playerName;


    @Getter
    @Setter
    private EncryptionRequestPacket lastEncryptionRequest;

    @Getter
    @Setter
    private HandshakePacket lastHandshake;

    @Getter
    @Setter
    private LoginRequestPacket lastLoginRequest;



    /**
     * Получение пакета по каналу.
     *
     * @param channelHandlerContext - канал.
     * @param byteBuf - данные.
     * @param list - лист с обьектами.
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        ChannelPacketBuffer channelPacketBuffer = new ChannelPacketBuffer(byteBuf);
        MinecraftPacketManager packetManager = AdvanceProxy.getInstance().getMinecraftPacketManager();

        int packetId = channelPacketBuffer.readVarInt();

        if (!packetManager.packetIsExists(packetId)) {
            //Клиенту приходит bad packet, если отправлять его обратно
            return;
        }

        MinecraftPacket minecraftPacket = packetManager.getNewPacket(protocolStatus, minecraftVersion.getVersionId(), packetId);

        if (minecraftPacket == null) {
            return;
        }

        if (AdvanceProxy.getInstance().getProxyConfig().getProxySettings().isLogReadPacket()) {
            AdvanceProxy.getInstance().getLogger().info("Read packet @" + minecraftPacket.getClass().getSimpleName() + "(" + packetId + ")");
        }

        minecraftPacket.readPacket(channelPacketBuffer, minecraftVersion.getVersionId());

        if (AdvanceProxy.getInstance().getProxyConfig().getProxySettings().isLogHandlePacket()) {
            AdvanceProxy.getInstance().getLogger().info("Handle packet @" + minecraftPacket.getClass().getSimpleName() + "(" + packetId + ")");
        }

        minecraftPacket.handle(channelHandlerContext.channel());
    }

}
