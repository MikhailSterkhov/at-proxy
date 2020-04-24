package net.advanceteam.proxy.netty.protocol.packet.impl.handshake;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.connection.server.impl.ProxyServer;
import net.advanceteam.proxy.netty.buffer.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.codec.MinecraftPacketDecoder;
import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;
import net.advanceteam.proxy.netty.protocol.status.ProtocolStatus;
import net.advanceteam.proxy.netty.protocol.version.MinecraftVersion;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandshakePacket implements MinecraftPacket {

    private int clientVersion;

    private String host;

    private int port;
    private int voidRequest;


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
        MinecraftVersion minecraftVersion = MinecraftVersion.getByVersionId( clientVersion );

        //decoder options set
        MinecraftPacketDecoder packetDecoder = channel.pipeline().get(MinecraftPacketDecoder.class);

        packetDecoder.setLastHandshake(this);
        packetDecoder.setMinecraftVersion(minecraftVersion);

        //update host
        AdvanceProxy.getInstance().setProxyHost(host);

        //check request
        switch (voidRequest) {
            case 1: {
                for (ProxyServer proxyServer : AdvanceProxy.getInstance().getProxies()) {
                    proxyServer.setHostAddress(host);
                }

                AdvanceProxy.getInstance().setProxyHost(host);
                AdvanceProxy.getInstance().getMinecraftPacketManager().setProtocolStatus(channel, ProtocolStatus.STATUS);
                break;
            }

            case 2: {
                AdvanceProxy.getInstance().getMinecraftPacketManager().setProtocolStatus(channel, ProtocolStatus.LOGIN);
                break;
            }
        }
    }
}
