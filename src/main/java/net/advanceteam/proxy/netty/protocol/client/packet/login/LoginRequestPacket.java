package net.advanceteam.proxy.netty.protocol.client.packet.login;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.chat.component.BaseComponent;
import net.advanceteam.proxy.common.chat.component.TextComponent;
import net.advanceteam.proxy.common.chat.serializer.ComponentSerializer;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.annotation.ClientPacketHandler;
import net.advanceteam.proxy.netty.protocol.client.codec.ClientPacketDecoder;
import net.advanceteam.proxy.netty.protocol.client.codec.ClientPacketEncoder;
import net.advanceteam.proxy.netty.protocol.client.packet.game.DisconnectPacket;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@ClientPacketHandler(packetQuery = "LOGIN_REQUEST_PACKET")
public class LoginRequestPacket implements ClientPacket {

    private String playerName;

    public LoginRequestPacket() {
        for (ClientVersion clientVersion : ClientVersion.values()) {
            registerClientPacket(clientVersion, 0x00);
        }
    }

    @Override
    public void writePacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        channelPacketBuffer.writeString(playerName);
    }

    @Override
    public void readPacket(ChannelPacketBuffer channelPacketBuffer, int clientVersion) {
        this.playerName = channelPacketBuffer.readString();
    }

    @Override
    public void handle(Channel channel) {
        if (playerName.length() > 16) {
            disconnect(channel, "§cДлина Вашего ника не должна привышать 16 символов\n" +
                    "§сДлина Вашего ника: §e" + playerName.length() + " символов");
        }

        if (playerName.contains(".")) {
            disconnect(channel, "§cВ Вашем нике содержатся недопустимые символы!");
            return;
        }

        if (AdvanceProxy.getInstance().getProxyConfig().getProxySettings().isProxyFullKick()
                && AdvanceProxy.getInstance().getOnlineCount() >= AdvanceProxy.getInstance().getProxyConfig().getMaxPlayers()) {
            disconnect(channel, "§cСервер переполнен, попробуйте зайти позже!");
            return;
        }

        ClientPacketDecoder clientPacketDecoder = channel.pipeline().get(ClientPacketDecoder.class);
        ClientPacketEncoder clientPacketEncoder = channel.pipeline().get(ClientPacketEncoder.class);

        clientPacketDecoder.setPacketType("GAME");
        clientPacketEncoder.setPacketType("GAME");


        UUID uuid = UUID.nameUUIDFromBytes( playerName.getBytes(StandardCharsets.UTF_8) );
        channel.writeAndFlush( new LoginSuccessPacket(uuid.toString(), playerName) );

        connect(uuid, channel);
    }

    private void disconnect(Channel channel, String reason) {
        disconnect(channel, TextComponent.fromLegacyText(reason));
    }

    private void disconnect(Channel channel, BaseComponent... reason) {
        channel.writeAndFlush(new DisconnectPacket(ComponentSerializer.toString(reason)));
        channel.close();
    }

    private void connect(UUID uuid, Channel channel) {
        List<String> permissions = new ArrayList<>();

        ClientPacketDecoder packetDecoder = channel.pipeline().get(ClientPacketDecoder.class);
        ClientVersion clientVersion = packetDecoder.getClientVersion();

        String serverName = AdvanceProxy.getInstance().getProxyConfig().getDefaultServer();

        Player player = new Player(
                playerName, uuid, null,

                (InetSocketAddress) channel.remoteAddress(),
                channel,

                permissions, clientVersion, false,
                packetDecoder.getLastHandshakePacket()
        );

        AdvanceProxy.getInstance().getPlayerManager().connectPlayer(player);

        player.connect(serverName);
    }
}
