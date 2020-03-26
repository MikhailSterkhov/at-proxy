package net.advanceteam.proxy;

import lombok.*;
import net.advanceteam.proxy.common.config.FileConfiguration;
import net.advanceteam.proxy.connection.server.ProxyServer;
import net.advanceteam.proxy.connection.server.BukkitServer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class ProxyConfiguration {

    @Getter
    private String defaultServer;

    @Getter
    private List<String> serverMotd;

    @Getter
    private int maxPlayers;

    @Getter
    private ConfigSettings proxySettings;

    @Getter
    private Map<String, BukkitServer> bukkitServerMap;

    @Getter
    private Map<String, ProxyServer> proxyServerMap;

    @Getter
    private FileConfiguration config;


    public String getServerHost(String serverName) {
        String serverAddress = config.getString("servers." + serverName);

        if ( !(serverAddress.contains(".") && serverAddress.contains(":")) ) {
            return null;
        }

        return serverAddress.split(":")[0];
    }

    public int getServerPort(String serverName) {
        String serverAddress = config.getString("servers." + serverName);

        if ( !(serverAddress.contains(".") && serverAddress.contains(":")) ) {
            return -1;
        }

        return Integer.parseInt( serverAddress.split(":")[1] );
    }


    /**
     * Загрузить конфигурацию
     */
    public void load() {
        this.defaultServer = config.getString("default_server");
        this.serverMotd = config.getStringList("server.motd");
        this.maxPlayers = config.getInt("server.max-players");

        this.proxySettings = ConfigSettings.newBuilder()
                .logWritePacket( config.getBoolean("settings.log_write_packet") )
                .logReadPacket( config.getBoolean("settings.log_read_packet") )
                .logHandlePacket( config.getBoolean("settings.log_handle_packet") )
                .logDispatchCommand( config.getBoolean("settings.log_dispatch_command") )
                .proxyFullKick( config.getBoolean("settings.proxy_full_kick") )
                .onlineMode( config.getBoolean("settings.online_mode") )
                .build();

        loadProxyServers();

        AdvanceProxy.getInstance().getLogger().info("Configuration has been loaded");
        AdvanceProxy.getInstance().getLogger().info("");
    }

    /**
     * Перезагрузить конфирурацию
     */
    public void reload() {
        File configFile = new File("config.yml");

        try {
            if ( !configFile.exists() ) {
                Files.copy(Objects.requireNonNull(ProxyConfiguration.class.getClassLoader().getResourceAsStream("config.yml")), configFile.toPath() );
            }

            this.config = AdvanceProxy.getInstance().getConfigManager().load(configFile);
            this.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Загрузить сервера из конфига
     */
    private void loadProxyServers() {
        this.bukkitServerMap = new HashMap<>();
        this.proxyServerMap = new HashMap<>();

        //proxies
        FileConfiguration proxiesSection = config.getSection("proxies");

        if ( proxiesSection == null ) {
            return;
        }

        for ( String proxyName : proxiesSection.getKeys() ) {
            String proxyHost = proxiesSection.getString(proxyName + ".host");
            int proxyPort = proxiesSection.getInt(proxyName + ".port");

            ProxyServer proxyServer = new ProxyServer(null, proxyName, proxyHost, proxyPort, false);
            proxyServerMap.put(proxyServer.getName(), proxyServer);
        }

        //bukkit
        FileConfiguration serversSection = config.getSection("servers");

        if ( serversSection == null ) {
            return;
        }

        for (String serverName : serversSection.getKeys()) {
            String serverAddress = serversSection.getString(serverName);

            if ( !(serverAddress.contains(".") && serverAddress.contains(":")) ) {
                continue;
            }

            String[] addressData = serverAddress.split(":");

            String serverHost = addressData[0];
            int serverPort = Integer.parseInt(addressData[1]);

            BukkitServer bukkitServer =  new BukkitServer(
                    null, serverName,
                    "Can`t connect to the server",
                    "Can`t connect to the server",
                    "Can`t connect to the server", serverHost, serverPort,
                    0, 0, 0, new ArrayList<>(), new ArrayList<>());

            bukkitServerMap.put(serverName.toLowerCase(), bukkitServer);
        }
    }


    /**
     * Конфигурационные настройки прокси
     */
    @Getter
    @Builder(builderMethodName = "newBuilder")
    public static class ConfigSettings {

        private boolean logWritePacket;
        private boolean logReadPacket;
        private boolean logHandlePacket;
        private boolean logDispatchCommand;
        private boolean proxyFullKick;
        private boolean onlineMode;
    }

}
