package net.advanceteam.proxy.netty.protocol.client.packet.login;

import com.google.common.base.Charsets;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.chat.component.BaseComponent;
import net.advanceteam.proxy.common.chat.component.TextComponent;
import net.advanceteam.proxy.common.chat.serializer.ComponentSerializer;
import net.advanceteam.proxy.common.event.impl.ProxyFullKickEvent;
import net.advanceteam.proxy.common.utility.EncryptionUtil;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.connection.server.impl.Server;
import net.advanceteam.proxy.netty.protocol.ChannelPacketBuffer;
import net.advanceteam.proxy.netty.protocol.client.ClientPacket;
import net.advanceteam.proxy.netty.protocol.client.annotation.ClientPacketHandler;
import net.advanceteam.proxy.netty.protocol.client.codec.ClientPacketDecoder;
import net.advanceteam.proxy.netty.protocol.client.compress.PacketCompressor;
import net.advanceteam.proxy.netty.protocol.client.compress.PacketDecompressor;
import net.advanceteam.proxy.netty.protocol.client.packet.game.DisconnectPacket;
import net.advanceteam.proxy.netty.protocol.client.version.ClientVersion;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@ClientPacketHandler(packetQuery = "LOGIN")
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
        ClientPacketDecoder packetDecoder = channel.pipeline().get(ClientPacketDecoder.class);

        packetDecoder.setLastLoginRequest(this);
        packetDecoder.setPlayerName(playerName);

        if (AdvanceProxy.getInstance().getProxyConfig().getProxySettings().isOnlineMode()) {
            EncryptionRequestPacket encryptionRequestPacket = EncryptionUtil.createEncryptionRequestPacket();

            packetDecoder.setLastEncryptionRequest(encryptionRequestPacket);
            channel.writeAndFlush(encryptionRequestPacket);
            return;
        }

        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes(Charsets.UTF_8));

        finish(channel, uuid, playerName);
    }

    public void disconnect(Channel channel, String reason) {
        disconnect(channel, TextComponent.fromLegacyText(reason));
    }

    private void disconnect(Channel channel, BaseComponent... reason) {
        channel.writeAndFlush(new DisconnectPacket(ComponentSerializer.toString(reason)));
        channel.close();
    }

    private void connect(UUID uuid, Channel channel, Server server) {
        List<String> permissions = new ArrayList<>();

        ClientPacketDecoder packetDecoder = channel.pipeline().get(ClientPacketDecoder.class);
        ClientVersion clientVersion = packetDecoder.getClientVersion();

        Player player = new Player (
                playerName, uuid, null,

                (InetSocketAddress) channel.remoteAddress(),
                channel,

                permissions, clientVersion, false,
                packetDecoder.getLastHandshake()
        );

        channel.writeAndFlush(new LoginSuccessPacket(uuid.toString(), playerName));

        AdvanceProxy.getInstance().getClientPacketManager().setPacketType(channel, "GAME");

        //TODO: Либо проблема в PluginMessagePacket, либо нужно еще кидать пакет playerTabList
        channel.writeAndFlush( AdvanceProxy.getInstance().registerChannels(clientVersion.getVersion()) );

        AdvanceProxy.getInstance().getPlayerManager().connectPlayer(player);
        player.connect(server);
    }

    public void finish(Channel channel, UUID uuid, String playerName) {
        //проверяем длину ника
        if (playerName.length() > 16) {
            disconnect(channel, "§cДлина Вашего ника не должна привышать 16 символов\n" +
                    "§сДлина Вашего ника: §e" + playerName.length() + " символов");
            return;
        }

        //проверяем наличие недопустимых символов
        if (playerName.contains(".")) {
            disconnect(channel, "§cВ Вашем нике содержатся недопустимые символы!");
            return;
        }

        //проверяем онлайн прокси, если она
        //заполнена, то кикаем игрока
        if (AdvanceProxy.getInstance().getProxyConfig().getProxySettings().isProxyFullKick()
                && AdvanceProxy.getInstance().getOnlineCount() >= AdvanceProxy.getInstance().getProxyConfig().getMaxPlayers()) {

            //но сначала чекнем ивент
            ProxyFullKickEvent fullKickEvent = new ProxyFullKickEvent(playerName, channel);
            AdvanceProxy.getInstance().getEventManager().callEvent(fullKickEvent);

            if (!fullKickEvent.isCancelled()) {
                disconnect(channel, "§cСервер переполнен, попробуйте зайти позже!");
                return;
            }
        }

        //проверяем, подключен ли игрок к серверу
        if (AdvanceProxy.getInstance().getPlayerManager().getPlayerMap().containsKey(playerName.toLowerCase())) {
            disconnect(channel, "§cДанный игрок уже подключен к серверу!");
            return;
        }

        channel.writeAndFlush(new SetCompressionPacket(256));
        setCompressionThreshold(channel, 256);

        Server server = AdvanceProxy.getInstance().getServer(
                AdvanceProxy.getInstance().getProxyConfig().getDefaultServer()
        );

        //проверяем, подключен ли стандартный сервер
        if (server == null) {
            disconnect(channel, "§cНевозможно подключиться к Default серверу: java.lang.NullPointerException");
            return;
        }

        //коннектим игрока если все норм
        channel.eventLoop().execute(() -> connect(uuid, channel, server));
    }

    private void setCompressionThreshold(Channel channel, int compressionThreshold) {
        if (channel.pipeline().get(PacketCompressor.class) == null && compressionThreshold != -1) {
            channel.pipeline().addBefore("packet-encoder", "compress", new PacketCompressor());
        }

        if (compressionThreshold != -1) {
            channel.pipeline().get(PacketCompressor.class).setThreshold(compressionThreshold);
        } else {
            channel.pipeline().remove("compress");
        }

        if (channel.pipeline().get(PacketDecompressor.class) == null && compressionThreshold != -1) {
            channel.pipeline().addBefore("packet-decoder", "decompress", new PacketDecompressor());
        }

        if (compressionThreshold == -1) {
            channel.pipeline().remove("decompress");
        }
    }

}
