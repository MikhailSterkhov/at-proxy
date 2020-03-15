package net.advanceteam.proxy.common.command;

import net.advanceteam.proxy.common.chat.ChatMessageType;
import net.advanceteam.proxy.common.command.type.CommandSendingType;

public interface CommandSender {

    String getName();

    CommandSendingType getCommandSendingType();

    void sendMessage(String message);

    void sendMessage(String... messages);

    void sendMessage(ChatMessageType messageType, String message);

    void sendMessage(ChatMessageType messageType, String... messages);
}
