package net.advanceteam.proxy.connection.console;

import lombok.Getter;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.chat.ChatMessageType;
import net.advanceteam.proxy.common.command.sender.CommandSender;
import net.advanceteam.proxy.common.command.type.CommandSendingType;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ConsoleSender implements CommandSender {

    private final CommandSendingType commandSendingType = CommandSendingType.CONSOLE;


    @Override
    public void sendMessage(String message) {
        AdvanceProxy.getInstance().getLogger().info(message);
    }

    @Override
    public void sendMessage(String... messages) {
        for (String message : messages) sendMessage(message);
    }

    @Override
    public void sendMessage(ChatMessageType messageType, String message) {
        sendMessage(message);
    }

    @Override
    public void sendMessage(ChatMessageType messageType, String... messages) {
        sendMessage(messages);
    }

    @Override
    public String getName() {
        return "Console";
    }

    @Override
    public List<String> getPermissions() {
        return new ArrayList<>();
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

}
