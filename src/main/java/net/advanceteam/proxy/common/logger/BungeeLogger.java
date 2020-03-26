package net.advanceteam.proxy.common.logger;

import jline.console.ConsoleReader;
import lombok.Getter;
import net.advanceteam.proxy.common.chat.ChatColor;
import org.fusesource.jansi.Ansi;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class BungeeLogger extends Logger {

    private final ConsoleReader consoleReader;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

    @Getter
    private final Map<ChatColor, String> replacements = new EnumMap<>(ChatColor.class);


    public BungeeLogger(ConsoleReader consoleReader) {
        super("proxy", null);
        this.consoleReader = consoleReader;

        checkLogsDirectory();

        setLevel(Level.ALL);

        try {
            FileHandler fileHandler = new FileHandler("logs" + File.separator + "proxy.log", 1 << 24, 8, true);
            fileHandler.setEncoding("UTF-8");

            addHandler(fileHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void log(LogRecord logRecord) {
        StringBuilder formatted = new StringBuilder();

        formatted.append(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString());
        formatted.append(ConsoleReader.RESET_LINE);

        formatted.append("| ");
        formatted.append(simpleDateFormat.format(logRecord.getMillis()));
        formatted.append("  [");
        formatted.append(logRecord.getLevel().getLocalizedName());
        formatted.append("] ");
        formatted.append(replaceColors(logRecord.getMessage()));
        formatted.append('\n');

        if ( logRecord.getThrown() != null ) {
            StringWriter writer = new StringWriter();

            logRecord.getThrown().printStackTrace(new PrintWriter(writer));

            formatted.append(ChatColor.RED);
            formatted.append(writer);
            formatted.append(ChatColor.RESET);
        }

        try {
            consoleReader.print(formatted.toString() + Ansi.ansi().reset().toString());
            consoleReader.drawLine();
            consoleReader.flush();
        } catch (Exception ignored) { }
    }

    private void checkLogsDirectory() {
        File logsDirectory = new File("logs");

        if (!logsDirectory.exists()) {
            logsDirectory.mkdir();
        }
    }

    private String replaceColors(String string) {
        String[] split = string.split("");

        for ( int i = 0; i < split.length - 1; i++ ) {
            if (! ((split[i].equals("?") || split[i].equals("§")) && ChatColor.ALL_CODES.contains(split[i + 1])) ) {
                continue;
            }

            split[i] = "";
            split[i + 1] = ChatColor.getByChar(split[i + 1].toCharArray()[0]).getAnsi().toString();
        }

        StringBuilder resultString = new StringBuilder();
        for (String character : split) {
            resultString.append(character);
        }

        return resultString.toString();
    }

}
