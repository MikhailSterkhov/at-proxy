package net.advanceteam.proxy.common.command;

import lombok.Getter;
import lombok.Setter;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.command.sender.CommandSender;
import net.advanceteam.proxy.common.plugin.ProxyPlugin;

@Getter
public abstract class CommandExecutor {

    private final String command;
    private String[] aliases;

    @Setter
    private ProxyPlugin plugin;


    public CommandExecutor(String command, String... aliases) {
        this.command = command;
        this.aliases = aliases;
    }

    /**
     * Зарегистрировать команду
     */
    public void register(ProxyPlugin proxyPlugin) {
        AdvanceProxy.getInstance().getCommandManager().registerCommand(proxyPlugin, this);
    }

    /**
     * Выполнение команды
     *
     * @param commandSender - отправитель
     * @param args - аргументы
     */
    public abstract void executeCommand(CommandSender commandSender, String[] args);

}
