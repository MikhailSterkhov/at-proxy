package net.advanceteam.proxy.netty.protocol.packet.impl.status;

import com.google.gson.Gson;
import io.netty.channel.Channel;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.event.impl.ProxyPingEvent;
import net.advanceteam.proxy.common.ping.StatusResponseCallback;
import net.advanceteam.proxy.common.ping.icon.Favicon;
import net.advanceteam.proxy.connection.server.impl.ProxyServer;
import net.advanceteam.proxy.netty.buffer.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.codec.MinecraftPacketDecoder;
import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;
import net.advanceteam.proxy.netty.protocol.packet.impl.handshake.HandshakePacket;

public class StatusRequestPacket implements MinecraftPacket {

    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) { }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) { }

    @Override
    public void handle(Channel channel) {
        Gson gson = AdvanceProxy.getInstance().getGson();
        Favicon favicon = AdvanceProxy.getInstance().getServerFavicon();

        MinecraftPacketDecoder packetDecoder = channel.pipeline().get(MinecraftPacketDecoder.class);
        HandshakePacket handshakePacket = packetDecoder.getLastHandshake();

        for (ProxyServer proxyServer : AdvanceProxy.getInstance().getProxies()) {
            if (!handshakePacket.getHost().equals(proxyServer.getHostAddress()) && handshakePacket.getPort() != proxyServer.getPort()) {
                continue;
            }

            StatusResponseCallback statusResponse = new StatusResponseCallback(
                    new StatusResponseCallback.Version(proxyServer.getName(), packetDecoder.getMinecraftVersion().getVersionId()),
                    new StatusResponseCallback.Players(proxyServer.getMaxSlots(), proxyServer.getOnlineCount(), null),
                    new StatusResponseCallback.Description(proxyServer.getMotd()),

                    favicon == null ? "" : favicon.getEncoded());

            //write packet
            channel.writeAndFlush(new StatusResponsePacket(gson.toJson(statusResponse)));
            channel.writeAndFlush(new StatusPingPacket());

            //event
            AdvanceProxy.getInstance().getEventManager().callEvent(new ProxyPingEvent(channel, statusResponse));
        }

    }

}
