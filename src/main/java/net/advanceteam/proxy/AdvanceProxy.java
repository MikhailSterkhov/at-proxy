package net.advanceteam.proxy;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import jline.console.ConsoleReader;
import lombok.Getter;
import lombok.Setter;
import net.advanceteam.proxy.common.chat.ChatColor;
import net.advanceteam.proxy.common.chat.component.BaseComponent;
import net.advanceteam.proxy.common.chat.component.KeybindComponent;
import net.advanceteam.proxy.common.chat.component.SelectorComponent;
import net.advanceteam.proxy.common.chat.component.TranslatableComponent;
import net.advanceteam.proxy.common.chat.serializer.*;
import net.advanceteam.proxy.common.command.impl.*;
import net.advanceteam.proxy.common.command.manager.CommandManager;
import net.advanceteam.proxy.common.config.ConfigurationManager;
import net.advanceteam.proxy.common.event.impl.ProxyReloadEvent;
import net.advanceteam.proxy.common.event.manager.EventManager;
import net.advanceteam.proxy.common.logger.BungeeLogger;
import net.advanceteam.proxy.common.mail.MailManager;
import net.advanceteam.proxy.common.ping.icon.Favicon;
import net.advanceteam.proxy.common.plugin.manager.PluginManager;
import net.advanceteam.proxy.common.scheduler.ProxyScheduler;
import net.advanceteam.proxy.common.scheduler.SchedulerManager;
import net.advanceteam.proxy.connection.manager.PlayerManager;
import net.advanceteam.proxy.connection.manager.ServerManager;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.connection.server.Server;
import net.advanceteam.proxy.connection.server.impl.ProxyServer;
import net.advanceteam.proxy.netty.bootstrap.BootstrapManager;
import net.advanceteam.proxy.netty.protocol.manager.MinecraftPacketManager;
import net.advanceteam.proxy.netty.protocol.packet.impl.game.PluginMessagePacket;
import net.advanceteam.proxy.netty.protocol.version.MinecraftVersion;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class AdvanceProxy {

    @Getter
    private final MinecraftPacketManager minecraftPacketManager = new MinecraftPacketManager();

    @Getter
    private final ConfigurationManager configManager = new ConfigurationManager();

    @Getter
    private final SchedulerManager schedulerManager = new SchedulerManager();

    @Getter
    private final BootstrapManager bootstrapManager = new BootstrapManager();

    @Getter
    private final CommandManager commandManager = new CommandManager();

    @Getter
    private final PluginManager pluginManager = new PluginManager();

    @Getter
    private final ConsoleReader consoleReader = new ConsoleReader();

    @Getter
    private final ServerManager serverManager = new ServerManager();

    @Getter
    private final PlayerManager playerManager = new PlayerManager();

    @Getter
    private final EventManager eventManager = new EventManager();

    @Getter
    private final MailManager mailManager = new MailManager();

    @Getter
    private final Collection<String> pluginChannels = new HashSet<>();

    @Getter
    private final Logger logger = new BungeeLogger(consoleReader);

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
    private final File pluginsFolder = new File("plugins");

    @Getter
    private final ProxyConfiguration proxyConfig;

    @Getter
    @Setter
    private String proxyHost;


// ============================================= INSTANCE ========================================================== //

    @Getter
    private static AdvanceProxy instance;

    public AdvanceProxy() throws IOException {
        instance = this;

        this.pluginsFolder.mkdir();
        this.proxyConfig = new ProxyConfiguration();
    }

// ================================================================================================================ //


    /**
     * Запуск AdvanceProxy ! !!! !!!! !!!!!!  !!! ! ! !!!  !!! !!! !!!! : )):):). :( не запустится отвечаю :(:(:(:(
     */
    public void start(long startMills) throws Exception {
        pluginManager.loadPlugins();

        //Регистрация команд.
        commandManager.registerCommand(null, new PluginsCommand());
        commandManager.registerCommand(null, new ReloadCommand());
        commandManager.registerCommand(null, new StopCommand());
        commandManager.registerCommand(null, new ClearConsoleCommand());
        commandManager.registerCommand(null, new HelpCommand());

        //Загрузка конфигурации Bungee.
        proxyConfig.reload();

        //Бинд серверов.
        bindProxies();

        new ProxyScheduler("proxy-servers-bind-312") {

            @Override
            public void run() {
                bindServers();
            }

        }.runTimer(0, 5, TimeUnit.SECONDS);

        //log about completed start
        logger.info("");
        logger.info(String.format("%sDone(%sms): AdvanceProxy has been started!", ChatColor.GREEN, System.currentTimeMillis() - startMills));
        logger.info(ChatColor.GREEN + "Type \"/ghelp\" to show a Proxy commands.");

        registerChannel("BungeeCord");
    }

    /**
     * Перезагрузить Proxy
     */
    public void reload() {
        ProxyReloadEvent proxyReloadEvent = new ProxyReloadEvent();
        eventManager.callEvent(proxyReloadEvent);

        if (proxyReloadEvent.isCancelled()) {
            return;
        }

        proxyConfig.reload();
        pluginManager.reloadPlugins();

        AdvanceProxy.getInstance().getLogger().log(Level.INFO, "§eAdvanceProxy has been reloaded. Thanks for using!");
    }

    public void shutdown() {
        eventManager.getRegisteredEventMethods().clear();
        eventManager.getRegisteredListeners().clear();

        commandManager.getCommandTable().clear();

        pluginManager.getPlugins().forEach(pluginManager::disablePlugin);

        AdvanceProxy.getInstance().getLogger().log(Level.INFO, "§aAdvanceProxy is closed. Thanks for using!");

        consoleReader.close();
        System.exit(0);
    }

    private void bindProxies() {
        for (ProxyServer proxyServer : proxyConfig.getProxyServerMap().values()) {
            bootstrapManager.bindServer(proxyServer.getInetAddress(), future -> {

                proxyServer.setServerChannel( future.channel() );

                if (future.isSuccess()) {
                    if (!proxyServer.isConnected()) {
                        logger.info(String.format("[Proxy] %s successfully handled bind", proxyServer));

                        proxyServer.setConnected(true);
                    }

                    return;
                }

                if (proxyServer.isConnected()) {
                    logger.info(String.format("[Proxy] %s failed to handle bind", proxyServer));

                    proxyServer.setConnected(false);
                }
            });
        }
    }

    private void bindServers() {
        // ping bukkit servers
        proxyConfig.getBukkitServerMap().values().forEach(server -> {
            ChannelFutureListener channelFutureListener = future -> {

                // init server channel
                server.setServerChannel( future.channel() );

                if (future.isSuccess()) {
                    if (!serverManager.hasServer(server)) {
                        serverManager.connectServer(server);
                    }

                    return;
                }

                if (serverManager.hasServer(server)) {
                    serverManager.disconnectServer(server.getName());
                }
            };

            bootstrapManager.connectServerToProxy(server, eventLoops, channelFutureListener);
        });
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

            Image image = new ImageIcon(faviconFile.toURL()).getImage();
            BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

            Graphics2D graphics2D = bufferedImage.createGraphics();
            graphics2D.drawImage(image, 0, 0, null);
            graphics2D.dispose();

            return Favicon.create(bufferedImage);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Зарегистрировать передачу каналов между
     * серверами
     *
     * @param protocolVersion - версия клиента
     */
    public PluginMessagePacket registerChannels(int protocolVersion) {
        if ( protocolVersion >= MinecraftVersion.V1_13.getVersionId()) {
            return new PluginMessagePacket("minecraft:register", Joiner.on("\00").join(pluginChannels.stream().map(PluginMessagePacket.MODERNISE::apply).collect(Collectors.toList())).getBytes(Charsets.UTF_8), false );
        }

        return new PluginMessagePacket("REGISTER", Joiner.on("\00").join(pluginChannels).getBytes(Charsets.UTF_8), false );
    }

    /**
     * Зарегистрировать каналы для PluginMessage
     *
     * @param channel - канал
     */
    public void registerChannel(String channel) {
        pluginChannels.add(channel);
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
