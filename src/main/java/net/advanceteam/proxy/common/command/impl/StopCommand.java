package net.advanceteam.proxy.common.command.impl;

import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.command.sender.CommandSender;
import net.advanceteam.proxy.common.command.CommandExecutor;

public class StopCommand extends CommandExecutor {

    public StopCommand() {
        super("gstop");
    }

    @Override
    public void executeCommand(CommandSender commandSender, String[] args) {
        AdvanceProxy.getInstance().shutdown();
    }

}
