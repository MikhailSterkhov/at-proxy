package net.advanceteam.proxy.netty.protocol.client.packet.handshake;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.connection.server.ProxyServer;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.annotation.ClientPacketHandler;
import net.advanceteam.proxy.netty.protocol.client.codec.ClientPacketDecoder;
import net.advanceteam.proxy.netty.protocol.client.codec.ClientPacketEncoder;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

@Getter
@AllArgsConstructor
@ClientPacketHandler(packetQuery = "HANDSHAKE_PACKET")
public class HandshakePacket implements ClientPacket {

    private int clientVersion;

    private String host;

    private int port;
    private int voidRequest;


    public HandshakePacket() {
        for (ClientVersion clientVersion : ClientVersion.values()) {
            registerClientPacket(clientVersion, 0x00);
        }
    }

    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        channelPacketBuffer.writeVarInt(clientVersion);
        channelPacketBuffer.writeString(host);
        channelPacketBuffer.writeShort(port);
        channelPacketBuffer.writeVarInt(voidRequest);
    }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        this.clientVersion = channelPacketBuffer.readVarInt();
        this.host = channelPacketBuffer.readString();
        this.port = channelPacketBuffer.readUnsignedShort();
        this.voidRequest = channelPacketBuffer.readVarInt();
    }

    @Override
    public void handle(Channel channel) {
        //init
        ClientVersion clientVersion = ClientVersion.getVersion( this.clientVersion );

        ClientPacketDecoder clientPacketDecoder = channel.pipeline().get(ClientPacketDecoder.class);
        ClientPacketEncoder clientPacketEncoder = channel.pipeline().get(ClientPacketEncoder.class);

        //set version
        clientPacketDecoder.setClientVersion(clientVersion);
        clientPacketEncoder.setClientVersion(clientVersion);

        //update last handshake
        clientPacketDecoder.setLastHandshakePacket(this);

        //update address
        for (ProxyServer proxyServer : AdvanceProxy.getInstance().getProxies()) {
            proxyServer.setHostAddress(host);
        }

        //check request
        switch (voidRequest) {
            case 1: {
                clientPacketDecoder.setPacketType("STATUS_REQUEST_PACKET");
                clientPacketEncoder.setPacketType("STATUS_REQUEST_PACKET");

                break;
            }

            case 2: {
                clientPacketDecoder.setPacketType("LOGIN_REQUEST_PACKET");
                clientPacketEncoder.setPacketType("LOGIN_REQUEST_PACKET");

                break;
            }
        }
    }
}
