package net.advanceteam.proxy;

import jline.console.ConsoleReader;
import net.advanceteam.proxy.common.chat.ChatColor;
import net.advanceteam.proxy.connection.console.ConsoleSender;
import org.fusesource.jansi.Ansi;

public class ProxyBootstrap {

    /**
     * Презапуск AdvanceTeam Proxy.
     *
     * @param args - аргументы.
     */
    public static void main(String[] args) throws Exception {
        System.out.println();
        System.out.println("          ------------------------------------------------------------------");
        System.out.println("          |                                                                |");
        System.out.println("          |                     >  AdvanceTeam Proxy  <                    |");
        System.out.println("          |                         [Proxy authors]:                       |");
        System.out.println("          |                                                                |");
        System.out.println("          |         ItzStonlex                          GitCoder           |");
        System.out.println("          |   https://vk.com/itzstonlex         https://vk.com/james_the   |");
        System.out.println("          |                                                                |");
        System.out.println("          ------------------------------------------------------------------");
        System.out.println();
        System.out.println("| Initializing AdvanceTeam Proxy...");
        System.out.println();
        System.out.println();

        long startMills = System.currentTimeMillis();

        AdvanceProxy advanceProxy = new AdvanceProxy();
        advanceProxy.start(startMills);

        String line;
        String linePrompt = Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + ConsoleReader.RESET_LINE
                + Ansi.ansi().fg(Ansi.Color.GREEN).toString()
                + "> "
                + Ansi.ansi().reset().toString();

        while ((line = advanceProxy.getConsoleReader().readLine(linePrompt)) != null) {
            if (!advanceProxy.getCommandManager().dispatchCommand(new ConsoleSender(), line)) {
                advanceProxy.getLogger().info(ChatColor.RED + "Неизвестная команда :c");
            }
        }
    }
}
