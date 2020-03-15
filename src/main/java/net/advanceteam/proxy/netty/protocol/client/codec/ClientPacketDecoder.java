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
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

import java.util.List;

public class ClientPacketDecoder extends ByteToMessageDecoder {

    @Setter
    @Getter
    private String packetType = "HANDSHAKE_PACKET";

    @Setter
    @Getter
    private ClientVersion clientVersion = ClientVersion.V1_15_1;

    @Getter
    @Setter
    private HandshakePacket lastHandshakePacket;


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

        System.out.println("decode " + clientPacket.getClass().getSimpleName());

        clientPacket.readPacket(channelPacketBuffer, clientVersion.getVersion());
        clientPacket.handle(channelHandlerContext.channel());
    }
}
