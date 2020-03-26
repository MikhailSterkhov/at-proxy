package net.advanceteam.proxy.common.command.manager;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.ProxyConfiguration;
import net.advanceteam.proxy.common.command.sender.CommandSender;
import net.advanceteam.proxy.common.command.CommandExecutor;
import net.advanceteam.proxy.common.plugin.ProxyPlugin;

import java.util.Arrays;

public final class CommandManager {

    @Getter
    private final Table<String, String, CommandExecutor> commandTable = HashBasedTable.create();

    /**
     * Выполнить команду от имени отправтеля
     *
     * @param commandSender - отправитель
     * @param command - команда
     */
    public boolean dispatchCommand(CommandSender commandSender, String command) {
        String[] commandArg = command.replaceFirst("/", "").split(" ", -1);

        CommandExecutor commandExecutor = getCommand(commandArg[0]);
        ProxyConfiguration proxyConfiguration = AdvanceProxy.getInstance().getProxyConfig();

        if (commandExecutor == null) {
            return false;
        }

        commandExecutor.executeCommand(commandSender, Arrays.copyOfRange(commandArg, 1, commandArg.length));

        if (proxyConfiguration.getProxySettings().isLogDispatchCommand()) {
            AdvanceProxy.getInstance().getLogger().info(commandSender.getName() + " has dispatched command: /" + command);
        }

        return true;
    }

    /**
     * Зарегистрировать команду
     *
     * @param commandExecutor - команда
     */
    public void registerCommand(ProxyPlugin proxyPlugin, CommandExecutor commandExecutor) {
        String pluginName = proxyPlugin == null ? "AdvanceProxy" : proxyPlugin.getPluginInfo().name();

        if (proxyPlugin != null) {
            commandExecutor.setPlugin(proxyPlugin);
        }

        commandTable.put(pluginName, commandExecutor.getCommand(), commandExecutor);

        for (String commandAlias : commandExecutor.getAliases()) {
            commandTable.put(pluginName, commandAlias, commandExecutor);
        }
    }

    /**
     * Проверяет, зарегистрирована ли команда
     *
     * @param commandName - имя команды
     */
    public boolean commandIsExists(String commandName) {
        return getCommand(commandName) != null;
    }

    /**
     * Получить команду по ее названию
     *
     * @param commandName - имя команды
     */
    public CommandExecutor getCommand(String commandName) {
        for (String pluginName : commandTable.rowKeySet()) {
            CommandExecutor commandExecutor = commandTable.get(pluginName, commandName);

            if (commandExecutor == null) {
                continue;
            }

            return commandExecutor;
        }

        return null;
    }

}
