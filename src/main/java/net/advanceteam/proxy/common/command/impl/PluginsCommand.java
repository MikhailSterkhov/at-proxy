package net.advanceteam.proxy.common.command.impl;

import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.command.sender.CommandSender;
import net.advanceteam.proxy.common.command.CommandExecutor;

import java.util.List;

public class PluginsCommand extends CommandExecutor {

    public PluginsCommand() {
        super("gplugins", "gpl");
    }

    @Override
    public void executeCommand(CommandSender commandSender, String[] args) {
        List<String> plugins = AdvanceProxy.getInstance().getPluginManager().getPluginNames();

        if (plugins.isEmpty()) {
            commandSender.sendMessage("§cНа данный момент на сервере нет плагинов :c");
            return;
        }

        commandSender.sendMessage("§6AdvanceProxy §6| §fСписок плагинов: §a" + String.join("§f, §a", plugins));
    }

}
