package net.advanceteam.proxy.netty.protocol.client.packet.status;

import com.google.gson.Gson;
import io.netty.channel.Channel;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.event.impl.ProxyPingEvent;
import net.advanceteam.proxy.common.ping.StatusResponseCallback;
import net.advanceteam.proxy.common.ping.icon.Favicon;
import net.advanceteam.proxy.connection.server.ProxyServer;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.annotation.ClientPacketHandler;
import net.advanceteam.proxy.netty.protocol.client.codec.ClientPacketDecoder;
import net.advanceteam.proxy.netty.protocol.client.codec.ClientPacketEncoder;
import net.advanceteam.proxy.netty.protocol.client.packet.handshake.HandshakePacket;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

@ClientPacketHandler(packetQuery = "STATUS_REQUEST_PACKET")
public class StatusRequestPacket implements ClientPacket {

    public StatusRequestPacket() {
        for (ClientVersion clientVersion : ClientVersion.values()) {
            registerClientPacket(clientVersion, 0x00);
        }
    }

    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) { }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) { }

    @Override
    public void handle(Channel channel) {
        Gson gson = AdvanceProxy.getInstance().getGson();
        Favicon favicon = AdvanceProxy.getInstance().getServerFavicon();

        ClientPacketDecoder packetDecoder = channel.pipeline().get(ClientPacketDecoder.class);
        HandshakePacket handshakePacket = packetDecoder.getLastHandshakePacket();

        for (ProxyServer proxyServer : AdvanceProxy.getInstance().getProxies()) {
            if (!handshakePacket.getHost().equals(proxyServer.getHostAddress()) && handshakePacket.getPort() != proxyServer.getPort()) {
                continue;
            }

            StatusResponseCallback statusResponse = new StatusResponseCallback(
                    new StatusResponseCallback.Version(proxyServer.getName(), packetDecoder.getClientVersion().getVersion()),
                    new StatusResponseCallback.Players(proxyServer.getMaxSlots(), proxyServer.getOnlineCount(), null),
                    new StatusResponseCallback.Description(proxyServer.getMotd()),

                    favicon == null ? "" : favicon.getEncoded());

            //write packet
            channel.writeAndFlush(new StatusResponsePacket(gson.toJson(statusResponse)), channel.voidPromise());

            channel.pipeline().get(ClientPacketDecoder.class).setPacketType("STATUS_PING_PACKET");
            channel.pipeline().get(ClientPacketEncoder.class).setPacketType("STATUS_PING_PACKET");

            channel.writeAndFlush(new StatusPingPacket());

            //event
            AdvanceProxy.getInstance().getEventManager().callEvent(new ProxyPingEvent(channel, statusResponse));
        }

    }

}
