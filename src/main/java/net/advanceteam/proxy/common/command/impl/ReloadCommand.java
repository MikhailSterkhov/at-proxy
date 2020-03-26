package net.advanceteam.proxy.common.command.impl;

import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.command.sender.CommandSender;
import net.advanceteam.proxy.common.command.CommandExecutor;

public class ReloadCommand extends CommandExecutor {

    public ReloadCommand() {
        super("greload", "grl");
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        AdvanceProxy.getInstance().reload();
    }

}
