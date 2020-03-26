package net.advanceteam.proxy.common.command.impl;

import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.command.sender.CommandSender;
import net.advanceteam.proxy.common.command.CommandExecutor;
import net.advanceteam.proxy.connection.player.Player;

import java.io.IOException;

public class ClearConsoleCommand extends CommandExecutor {

    public ClearConsoleCommand() {
        super("gclear", "gconsoleclear");
    }

    @Override
    public void executeCommand(CommandSender commandSender, String[] args) {
        if (commandSender instanceof Player) {
            return;
        }

        try {
            AdvanceProxy.getInstance().getConsoleReader().clearScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
