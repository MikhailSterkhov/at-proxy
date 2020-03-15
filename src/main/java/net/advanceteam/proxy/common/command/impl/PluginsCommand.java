package net.advanceteam.proxy.common.command.impl;

import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.command.CommandSender;
import net.advanceteam.proxy.common.command.execution.CommandExecutor;

import java.util.List;

public class PluginsCommand extends CommandExecutor {

    public PluginsCommand() {
        super("plugins");
    }

    @Override
    public void executeCommand(CommandSender commandSender, String[] args) {
        List<String> plugins = AdvanceProxy.getInstance().getPluginManager().getPluginNames();

        commandSender.sendMessage("§6AdvanceProxy §6| §fСписок плагинов: §a" + String.join("§f, §a", plugins));
    }

}
