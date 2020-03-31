package net.advanceteam.proxy.connection.server.impl;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.connection.server.Server;
import net.advanceteam.proxy.netty.protocol.client.packet.game.PluginMessagePacket;

import java.util.List;

@AllArgsConstructor
@Getter
public class BukkitServer implements Server {

    @Setter
    private Channel serverChannel;

    private final String name;
    private final String motd;
    private final String gameVersion;
    private final String worldName;
    private final String hostAddress;

    private final int port;
    private final int onlineCount;
    private final int worldsCount;
    private final int maxSlots;

    private final List<Player> onlinePlayers;
    private final List<String> worldNames;


    @Override
    public String toString() {
        return getInetAddress() + "(" + name + ")";
    }

    @Override
    public void sendData(String channel, byte[] bytes) {
        serverChannel.writeAndFlush(new PluginMessagePacket(channel, bytes, false));
    }
}
