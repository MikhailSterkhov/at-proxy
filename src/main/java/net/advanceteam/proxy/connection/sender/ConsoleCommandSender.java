package net.advanceteam.proxy.connection.sender;

import lombok.Getter;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.chat.ChatMessageType;
import net.advanceteam.proxy.common.command.CommandSender;
import net.advanceteam.proxy.common.command.type.CommandSendingType;

import java.util.logging.Logger;

@Getter
public class ConsoleCommandSender implements CommandSender {

    private final Logger logger = AdvanceProxy.getInstance().getLogger();

    private final CommandSendingType commandSendingType = CommandSendingType.CONSOLE;


    @Override
    public void sendMessage(String message) {
        logger.info(message);
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
}
