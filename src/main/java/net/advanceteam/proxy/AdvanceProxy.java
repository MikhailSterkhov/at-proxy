package net.advanceteam.proxy;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import jline.console.ConsoleReader;
import lombok.Getter;
import net.advanceteam.proxy.common.chat.ChatColor;
import net.advanceteam.proxy.common.chat.component.BaseComponent;
import net.advanceteam.proxy.common.chat.component.KeybindComponent;
import net.advanceteam.proxy.common.chat.component.SelectorComponent;
import net.advanceteam.proxy.common.chat.component.TranslatableComponent;
import net.advanceteam.proxy.common.chat.serializer.*;
import net.advanceteam.proxy.common.command.impl.PluginsCommand;
import net.advanceteam.proxy.common.command.manager.CommandManager;
import net.advanceteam.proxy.common.config.ConfigurationManager;
import net.advanceteam.proxy.common.event.manager.EventManager;
import net.advanceteam.proxy.common.logger.BungeeLogger;
import net.advanceteam.proxy.common.mail.MailManager;
import net.advanceteam.proxy.common.ping.icon.Favicon;
import net.advanceteam.proxy.common.plugin.manager.PluginManager;
import net.advanceteam.proxy.common.scheduler.SchedulerManager;
import net.advanceteam.proxy.connection.server.impl.Server;
import net.advanceteam.proxy.connection.server.ProxyServer;
import net.advanceteam.proxy.connection.manager.PlayerManager;
import net.advanceteam.proxy.connection.manager.ServerManager;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.netty.protocol.client.manager.ClientPacketManager;
import net.advanceteam.proxy.netty.protocol.client.packet.game.*;
import net.advanceteam.proxy.netty.protocol.client.packet.handshake.HandshakePacket;
import net.advanceteam.proxy.netty.protocol.client.packet.login.LoginRequestPacket;
import net.advanceteam.proxy.netty.protocol.client.packet.login.LoginSuccessPacket;
import net.advanceteam.proxy.netty.protocol.client.packet.status.StatusPingPacket;
import net.advanceteam.proxy.netty.protocol.client.packet.status.StatusRequestPacket;
import net.advanceteam.proxy.netty.protocol.client.packet.status.StatusResponsePacket;
import net.advanceteam.proxy.netty.system.BootstrapStarter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public final class AdvanceProxy {

    @Getter
    private final ClientPacketManager clientPacketManager = new ClientPacketManager();

    @Getter
    private final ConfigurationManager configManager = new ConfigurationManager();

    @Getter
    private final SchedulerManager schedulerManager = new SchedulerManager();

    @Getter
    private final CommandManager commandManager = new CommandManager();

    @Getter
    private final PluginManager pluginManager = new PluginManager();

    @Getter
    private final ServerManager serverManager = new ServerManager();

    @Getter
    private final PlayerManager playerManager = new PlayerManager();

    @Getter
    private final ConsoleReader consoleReader = new ConsoleReader();

    @Getter
    private final EventManager eventManager = new EventManager();

    @Getter
    private final MailManager mailManager = new MailManager();

    @Getter
    private final File pluginsFolder = new File("plugins");

    @Getter
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(BaseComponent.class, new ComponentSerializer())
            .registerTypeAdapter(TextComponent.class, new TextComponentSerializer())
            .registerTypeAdapter(TranslatableComponent.class, new TranslatableComponentSerializer())
            .registerTypeAdapter(KeybindComponent.class, new KeybindComponentSerializer())
            .registerTypeAdapter(SelectorComponent.class, new SelectorComponentSerializer())
            .registerTypeAdapter(Favicon.class, Favicon.FAVICON_TYPE_ADAPTER)
            .create();

    @Getter
    private final EventLoopGroup eventLoops = new NioEventLoopGroup(4, new ThreadFactoryBuilder()
            .setNameFormat("Netty IO Thread #%1$d")
            .build());

    @Getter
    private final Logger logger = new BungeeLogger(consoleReader);

    @Getter
    private final ProxyConfiguration proxyConfig;

    @Getter
    private final BootstrapStarter bootstrapStarter = new BootstrapStarter();


// ============================================= INSTANCE ========================================================== //

    @Getter
    private static AdvanceProxy instance;

    AdvanceProxy() throws IOException {
        instance = this;

        this.pluginsFolder.mkdir();
        this.proxyConfig = new ProxyConfiguration();
    }

// ================================================================================================================ //


    /**
     * Запуск AdvanceProxy ! !!! !!!! !!!!!!  !!! ! ! !!!  !!! !!! !!!! : )):):). :( не запустится отвечаю :(:(:(:(
     */
    public void start(long startMills) {
        try {
            //Загрузка плагинов.
            pluginManager.loadPlugins("plugins");
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        // ========= // Регистрация пакетов. // ========= //
        //handshake
        new HandshakePacket();

        //status
        new StatusRequestPacket();
        new StatusResponsePacket();
        new StatusPingPacket();

        //login
        new LoginRequestPacket();
        new LoginSuccessPacket();

        //game
        new ChatPacket();
        new DisconnectPacket();
        new PlayerListHeaderFooterPacket();
        new PluginMessagePacket();
        new TitlePacket();
        // ========= // Регистрация пакетов. // ========= //

        //Регистрация команд.
        commandManager.registerCommand(new PluginsCommand());

        //Загрузка конфигурации Bungee.
        proxyConfig.load();

        //Бинд серверов.
        this.bindServers(startMills);
    }

    private void bindServers(long startMills) {
        // ping proxy server
        for (ProxyServer proxyServer : proxyConfig.getProxyServerMap().values()) {
            bootstrapStarter.bindServer(proxyServer.getInetAddress(), future -> {

                proxyServer.setServerChannel( future.channel() );

                if (!future.isSuccess()) {
                    logger.info( String.format("[Proxy] %s failed to handle bind", proxyServer) );
                    return;
                }

                logger.info( String.format("[Proxy] %s successfully handled bind", proxyServer) );

            });
        }

        // ping bukkit servers
        proxyConfig.getBukkitServerMap().values().forEach(server -> {
            ChannelFutureListener channelFutureListener = future -> {

                // init server channel
                server.setServerChannel( future.channel() );

                if ( future.isSuccess() ) {
                    AdvanceProxy.getInstance().getServerManager().connectServer(server);
                    return;
                }

                //TODO: Connect to server with connector
            };

            bootstrapStarter.connectServer(eventLoops, server.getInetAddress(), channelFutureListener);
        });

        logger.info("");
        logger.info(String.format("%sDone(%sms): AdvanceProxy has been started!", ChatColor.GREEN, System.currentTimeMillis() - startMills));
    }

    /**
     * Получить иконку сервера
     */
    public Favicon getServerFavicon() {
        try {

            File faviconFile = new File("server-icon.png");
            if ( !faviconFile.exists() ) {
                return null;
            }

            Image imageIcon = new ImageIcon(faviconFile.toURL()).getImage();
            return Favicon.create((BufferedImage) imageIcon);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Получить количество общего онлайна
     * на сети Bungee
     */
    public int getOnlineCount() {
        return getOnlinePlayers().size();
    }

    /**
     * Получить соединение игрока по его нику
     *
     * @param playerName - ник игрока
     */
    public Player getPlayer(String playerName) {
        return playerManager.getPlayer(playerName);
    }

    /**
     * Получить соединение игрока по его UUID
     *
     * @param uniqueId - UUID игрока
     */
    public Player getPlayer(UUID uniqueId) {
        return playerManager.getPlayer(uniqueId);
    }

    /**
     * Получить соединение сервера по его названия
     *
     * @param serverName - имя сервера
     */
    public Server getServer(String serverName) {
        return serverManager.getServer(serverName);
    }

    /**
     * Получить список доступных серверов
     * с сети Bungee
     */
    public Map<String, Server> getServers() {
        return serverManager.getServerMap();
    }

    /**
     * Получить список игроков в сети
     */
    public Collection<Player> getOnlinePlayers() {
        return playerManager.getPlayerMap().values();
    }

    /**
     * Получить список проксей
     */
    public Collection<ProxyServer> getProxies() {
        return proxyConfig.getProxyServerMap().values();
    }

}
