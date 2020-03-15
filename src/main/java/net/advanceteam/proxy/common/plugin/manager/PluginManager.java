package net.advanceteam.proxy.common.plugin.manager;

import lombok.NonNull;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.plugin.ProxyPlugin;
import net.advanceteam.proxy.common.plugin.annotation.PluginHandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public final class PluginManager {

    private final Map<String, ProxyPlugin> plugins = new HashMap<>();


    /**
     * Загрузить все плагины из дериктории
     *
     * @param directoryName - название директориии
     */
    public void loadPlugins(@NonNull String directoryName) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        File directory = new File(directoryName);
        Logger logger = AdvanceProxy.getInstance().getLogger();

        if (directory.exists() && directory.isDirectory()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {

                JarFile jarFile = new JarFile(file);
                Enumeration<JarEntry> enumeration = jarFile.entries();

                while (enumeration.hasMoreElements()) {
                    JarEntry jarEntry = enumeration.nextElement();

                    if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
                        continue;
                    }

                    String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
                    className = className.replace('/', '.');

                    if (className.contains(file.getName().replace(".jar", ""))) {

                        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]
                                {file.toURI().toURL()});

                        Class<?> pluginClass = urlClassLoader.loadClass(className);

                        ProxyPlugin proxyPlugin = (ProxyPlugin) pluginClass.newInstance();

                        PluginHandler pluginInfo = proxyPlugin.getClass().getDeclaredAnnotation(PluginHandler.class);

                        logger.info(String.format("[PluginManager] Loading %s version %s by %s",
                                pluginInfo.name(),
                                pluginInfo.version(),
                                pluginInfo.author()));

                        proxyPlugin.onLoad();

                        logger.info(String.format("[PluginManager] Running %s version %s by %s",
                                pluginInfo.name(), pluginInfo.version(), pluginInfo.author()));

                        proxyPlugin.onEnable();

                        addPlugin(pluginInfo.name(), proxyPlugin);
                        proxyPlugin.setPluginInfo(pluginInfo);
                    }
                }
            }
        }

        logger.info(String.format("[PluginManager] Bungee detected {%s}", plugins.size()) + " active plugins");

    }

    /**
     * Добавить плагин
     *
     * @param name - имя плагина
     * @param proxyPlugin - плагин
     */
    private void addPlugin(@NonNull String name, @NonNull ProxyPlugin proxyPlugin) {
        plugins.put(name, proxyPlugin);
    }

    /**
     * Получить плагин
     *
     * @param name - имя
     */
    public ProxyPlugin getPlugin(String name) {
        return plugins.get(name);
    }

    /**
     * Получить список плагинов
     */
    public List<ProxyPlugin> getPlugins() {
        return Collections.unmodifiableList(new ArrayList<>( plugins.values() ));
    }

    /**
     * Получить список имен плагинов
     */
    public List<String> getPluginNames() {
        List<String> pluginsName = new ArrayList<>();

        for (ProxyPlugin proxyPlugin : plugins.values()) {
            pluginsName.add(proxyPlugin.getPluginInfo().name());
        }

        return pluginsName;
    }

}
