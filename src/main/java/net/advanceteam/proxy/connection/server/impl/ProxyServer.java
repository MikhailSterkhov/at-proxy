package net.advanceteam.proxy.connection.server.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.chat.ChatColor;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.connection.server.Server;
import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProxyServer implements Server {

    @Setter
    private Channel serverChannel;

    private final String name;
    private final String motd = ChatColor.translateAlternateColorCodes('&', Joiner.on("\n").join(advanceBungee.getProxyConfig().getServerMotd()));
    private final String gameVersion = "1.8 - 1.15.1";
    private final String worldName = null;

    @Setter
    private String hostAddress;
    private final int port;

    private final int worldsCount = 0;
    private final int maxSlots = advanceBungee.getProxyConfig().getMaxPlayers();

    private final List<Player> onlinePlayers = new ArrayList<>();
    private final List<String> worldNames = new ArrayList<>();

    @Setter
    private boolean connected;


    /**
     * INSTANCE
     */
    private static final AdvanceProxy advanceBungee = AdvanceProxy.getInstance();


    @Override
    public String toString() {
        return getInetAddress() + "(" + name + ")";
    }

    @Override
    public final int getOnlineCount() {
        return advanceBungee.getOnlineCount();
    }

    @Override
    public void sendData(String channel, byte[] data) {
        Preconditions.checkNotNull(channel, "channel");
        Preconditions.checkNotNull(data, "data");

        Server server = (onlinePlayers.isEmpty()) ? null : onlinePlayers.iterator().next().getServer();

        if (server != null) {
            server.sendData(channel, data);
        }
    }

    @Override
    public void sendPacket(MinecraftPacket minecraftPacket) {
        serverChannel.writeAndFlush(minecraftPacket);
    }

}
