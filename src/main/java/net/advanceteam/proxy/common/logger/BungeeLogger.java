package net.advanceteam.proxy.common.logger;

import jline.console.ConsoleReader;
import net.advanceteam.proxy.common.logger.formatter.TextFormatter;
import net.advanceteam.proxy.common.logger.handler.ColouredWriter;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BungeeLogger extends Logger {

    public BungeeLogger(ConsoleReader consoleReader) {
        super("proxy", null);
        new File("logs").mkdir();

        setLevel(Level.INFO);

        try {
            ColouredWriter colouredHandler = new ColouredWriter(consoleReader);
            colouredHandler.setFormatter(new TextFormatter());
            colouredHandler.setLevel(Level.INFO);
            colouredHandler.setEncoding("UTF-8");

            FileHandler fileHandler = new FileHandler("logs" + File.separator + "proxy.log", 0, 1, true);
            fileHandler.setFormatter(new TextFormatter());
            fileHandler.setEncoding("UTF-8");

            addHandler(fileHandler);
            addHandler(colouredHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
