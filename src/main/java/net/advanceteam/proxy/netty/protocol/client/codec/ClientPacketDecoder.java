package net.advanceteam.proxy.netty.protocol.client.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.Getter;
import lombok.Setter;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.manager.ClientPacketManager;
import net.advanceteam.proxy.netty.protocol.client.packet.handshake.HandshakePacket;
import net.advanceteam.proxy.netty.protocol.client.packet.login.EncryptionRequestPacket;
import net.advanceteam.proxy.netty.protocol.client.packet.login.LoginRequestPacket;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

import java.util.List;

public class ClientPacketDecoder extends ByteToMessageDecoder {

    @Setter
    @Getter
    private ClientVersion clientVersion = ClientVersion.V1_15_1;

    @Setter
    @Getter
    private String packetType = "HANDSHAKE_PACKET";



// ======================== // надо... // ======================== //
    @Getter
    @Setter
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

// ======================== // надо... // ======================== //



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
        ClientPacketManager packetManager = AdvanceProxy.getInstance().getClientPacketManager();

        int packetId = channelPacketBuffer.readVarInt();

        if (!packetManager.packetIsExists(packetId)) {
            return;
        }

        ClientPacket clientPacket = packetManager.getNewPacket(packetType, clientVersion.getVersion(), packetId);

        if (AdvanceProxy.getInstance().getProxyConfig().getProxySettings().isLogReadPacket()) {
            AdvanceProxy.getInstance().getLogger().info("Reading packet @" + clientPacket.getClass().getSimpleName() + "(" + packetId + ")");
        }

        clientPacket.readPacket(channelPacketBuffer, clientVersion.getVersion());

        if (AdvanceProxy.getInstance().getProxyConfig().getProxySettings().isLogHandlePacket()) {
            AdvanceProxy.getInstance().getLogger().info("Handling packet @" + clientPacket.getClass().getSimpleName() + "(" + packetId + ")");
        }

        clientPacket.handle(channelHandlerContext.channel());
    }
}
