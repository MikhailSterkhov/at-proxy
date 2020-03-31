package net.advanceteam.proxy.common.command.impl;

import net.advanceteam.proxy.common.command.CommandExecutor;
import net.advanceteam.proxy.common.command.sender.CommandSender;
import net.advanceteam.proxy.connection.console.ConsoleSender;

public class HelpCommand extends CommandExecutor {

    public HelpCommand() {
        super("ghelp", "proxy-help", "advanceproxy", "proxy");
    }

    @Override
    public void executeCommand(CommandSender commandSender, String[] args) {
        commandSender.sendMessage("§6AdvanceProxy §8| §fДанная система разработана §eItzStonlex §fи §eGitCoder");
        commandSender.sendMessage(" §fДоступные команды Proxy:");
        commandSender.sendMessage("  §6* §fПерезагрузить плагины и конфиг - §e/greload");
        commandSender.sendMessage("  §6* §fОстановить и выключить систему - §e/gstop");
        commandSender.sendMessage("  §6* §fВывести список плагинов - §e/gplugins");

        if (commandSender instanceof ConsoleSender) {
            commandSender.sendMessage("  §6* §fОчистить консоль - §e/gclear");
        }

        commandSender.sendMessage("§r");
        commandSender.sendMessage(" §fКонтакты разработчиков:");
        commandSender.sendMessage("  §6* §fItzStonlex (Миша Лейн): §ehttps://vk.com/itzstonlex");
        commandSender.sendMessage("  §6* §fGitCoder (Сергей Юрченко): §ehttps://vk.com/gitcoder");
    }

}
