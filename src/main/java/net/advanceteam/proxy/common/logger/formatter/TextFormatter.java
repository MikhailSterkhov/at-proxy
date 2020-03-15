package net.advanceteam.proxy.common.logger.formatter;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class TextFormatter extends Formatter {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

    @Override
    public String format(LogRecord record) {

        return "| " + simpleDateFormat.format(record.getMillis()) + "  [" + record.getLevel().getName() +"] " +
                formatMessage(record) +
                '\n';
    }
}
