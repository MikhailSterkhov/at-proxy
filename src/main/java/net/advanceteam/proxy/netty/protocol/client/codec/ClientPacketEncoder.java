package net.advanceteam.proxy.netty.protocol.client.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Getter;
import lombok.Setter;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.manager.ClientPacketManager;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

public class ClientPacketEncoder extends MessageToByteEncoder<ClientPacket> {

    @Setter
    @Getter
    private String packetType = "HANDSHAKE_PACKET";

    @Setter
    @Getter
    private ClientVersion clientVersion = ClientVersion.V1_15_1;


    /**
     * Отправка пакета по каналу.
     *
     * @param channelHandlerContext - канал.
     * @param clientPacket - пакет.
     * @param byteBuf - буфер обмена.
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ClientPacket clientPacket, ByteBuf byteBuf) {
        ChannelPacketBuffer channelPacketBuffer = new ChannelPacketBuffer(byteBuf);
        ClientPacketManager packetManager = AdvanceProxy.getInstance().getClientPacketManager();

        channelPacketBuffer.writeVarInt( packetManager.getPacketId(clientPacket.getClass(), clientVersion.getVersion()) );
        clientPacket.writePacket(channelPacketBuffer, clientVersion.getVersion());
    }
}
