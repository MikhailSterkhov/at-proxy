package net.advanceteam.proxy.common.command.manager;

import lombok.Getter;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.ProxyConfiguration;
import net.advanceteam.proxy.common.command.CommandSender;
import net.advanceteam.proxy.common.command.execution.CommandExecutor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    @Getter
    private final Map<String, CommandExecutor> commandMap = new HashMap<>();

    /**
     * Выполнить команду от имени отправтеля
     *
     * @param commandSender - отправитель
     * @param command - команда
     */
    public boolean dispatchCommand(CommandSender commandSender, String command) {
        String[] commandArg = command.replaceFirst("/", "").split(" ", -1);

        if ( !hasCommand(commandArg[0]) ) {
            return false;
        }

        CommandExecutor commandExecutor = getCommand(commandArg[0]);
        ProxyConfiguration proxyConfiguration = AdvanceProxy.getInstance().getProxyConfig();

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
    public void registerCommand(CommandExecutor commandExecutor) {
        commandMap.put(commandExecutor.getCommand(), commandExecutor);
    }

    /**
     * Зарегистрировать команду по ее названию
     *
     * @param commandName - имя команды
     * @param commandExecutor - команда
     */
    public void registerCommand(String commandName, CommandExecutor commandExecutor) {
        commandMap.put(commandName, commandExecutor);
    }

    /**
     * Проверяет, зарегистрирована ли команда
     *
     * @param commandName - имя команды
     */
    public boolean hasCommand(String commandName) {
        return getCommand(commandName) != null;
    }

    /**
     * Получить команду по ее названию
     *
     * @param commandName - имя команды
     */
    public CommandExecutor getCommand(String commandName) {
        return commandMap.get(commandName);
    }

}
