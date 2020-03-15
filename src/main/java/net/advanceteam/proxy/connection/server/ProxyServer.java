package net.advanceteam.proxy.connection.server;

import com.google.common.base.Joiner;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.chat.ChatColor;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.connection.server.impl.Server;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProxyServer implements Server {

    @Setter
    private Channel serverChannel;

    private final String name;
    private final String motd = ChatColor.translateAlternateColorCodes( '&', Joiner.on("\n").join(advanceBungee.getProxyConfig().getServerMotd()) );
    private final String gameVersion = "1.8 - 1.15.1";
    private final String worldName = null;

    @Setter
    private String hostAddress;
    private final int port;

    private final int onlineCount = advanceBungee.getOnlineCount();
    private final int worldsCount = 0;
    private final int maxSlots = advanceBungee.getProxyConfig().getMaxPlayers();

    private final List<Player> onlinePlayers = new ArrayList<>();
    private final List<String> worldNames = new ArrayList<>();


    /** INSTANCE */
    private static final AdvanceProxy advanceBungee = AdvanceProxy.getInstance();


    @Override
    public String toString() {
        return getInetAddress() + "(" + name + ")";
    }

}
