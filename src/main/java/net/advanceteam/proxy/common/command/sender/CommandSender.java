package net.advanceteam.proxy.common.command.sender;

import net.advanceteam.proxy.common.chat.ChatMessageType;
import net.advanceteam.proxy.common.command.type.CommandSendingType;

import java.util.List;

public interface CommandSender {

    String getName();

    CommandSendingType getCommandSendingType();

    List<String> getPermissions();

    boolean hasPermission(String permission);

    void sendMessage(String message);

    void sendMessage(String... messages);

    void sendMessage(ChatMessageType messageType, String message);

    void sendMessage(ChatMessageType messageType, String... messages);
}
