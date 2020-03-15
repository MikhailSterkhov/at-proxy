package net.advanceteam.proxy.common.command.execution;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.command.CommandSender;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public abstract class CommandExecutor {

    private final String command;
    private String[] aliases;

    /**
     * Зарегистрировать команду
     */
    public void register() {
        AdvanceProxy.getInstance().getCommandManager().registerCommand(command, this);
    }

    /**
     * Выполнение команды
     *
     * @param commandSender - отправитель
     * @param args - аргументы
     */
    public abstract void executeCommand(CommandSender commandSender, String[] args);

}
