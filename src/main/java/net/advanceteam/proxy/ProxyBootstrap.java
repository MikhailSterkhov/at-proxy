package net.advanceteam.proxy;

import net.advanceteam.proxy.connection.sender.ConsoleCommandSender;

public class ProxyBootstrap {

    /**
     * Презапуск AdvanceTeam Proxy.
     *
     * @param args - аргументы.
     */
    public static void main(String[] args) throws Exception {
        System.out.println();
        System.out.println("------------------------------------------------------------------");
        System.out.println("|                                                                |");
        System.out.println("|                   •  AdvanceTeam Proxy  •                      |");
        System.out.println("|                       [Proxy authors]:                         |");
        System.out.println("|                                                                |");
        System.out.println("|         ItzStonlex                          GitCoder           |");
        System.out.println("|   https://vk.com/itzstonlex         https://vk.com/james_the   |");
        System.out.println("|                                                                |");
        System.out.println("------------------------------------------------------------------");
        System.out.println();
        System.out.println("Initializing AT-Bungee by AdvanceTeam...");
        System.out.println();
        System.out.println();


        long startMills = System.currentTimeMillis();

        AdvanceProxy advanceProxy = new AdvanceProxy();
        advanceProxy.start(startMills);

        String line;
        while ((line = advanceProxy.getConsoleReader().readLine("> ")) != null) {
            if (!advanceProxy.getCommandManager().dispatchCommand(new ConsoleCommandSender(), line)) {
                advanceProxy.getLogger().info("§cUnknown command :(");
            }
        }
    }

}
