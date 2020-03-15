package net.advanceteam.proxy.common.plugin;

import lombok.Getter;
import lombok.Setter;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.config.FileConfiguration;
import net.advanceteam.proxy.common.plugin.annotation.PluginHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class ProxyPlugin {

    @Setter
    @Getter
    private PluginHandler pluginInfo;

    @Getter
    private FileConfiguration config;


    public abstract void onEnable();

    public void onLoad() { }

    public void onDisable() { }


    public AdvanceProxy getProxy() {
        return AdvanceProxy.getInstance();
    }

    public InputStream getResourceAsStream(String resourceName) {
        return getClass().getClassLoader().getResourceAsStream(resourceName);
    }

    public void saveDefaultConfig() {
        saveResource("config.yml");

        reloadConfig();
    }

    public void saveResource(String resourceName) {
        try {
            File pluginsFolder = AdvanceProxy.getInstance().getPluginsFolder();

            if (!pluginsFolder.exists()) {
                pluginsFolder.mkdir();
            }

            Path configPath = pluginsFolder.toPath().resolve(resourceName);

            if (Files.exists(configPath)) {
                return;
            }

            Files.copy(getResourceAsStream(resourceName), configPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void reloadConfig() {
        try {
            File pluginsFolder = AdvanceProxy.getInstance().getPluginsFolder();
            File configFile = new File(pluginsFolder, "config.yml");

            this.config = AdvanceProxy.getInstance().getConfigManager().load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
