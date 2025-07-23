package dev.shaaf.waver.log;

import picocli.CommandLine.Help.Ansi;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ColorFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        String level = record.getLevel().getName();
        String message = formatMessage(record);

        // Use a switch to apply color based on log level
        String colorMarkup = switch (record.getLevel().getName()) {
            case "SEVERE"  -> "@|fg(red) %s|@";
            case "WARNING" -> "@|fg(yellow) %s|@";
            case "INFO"    -> "@|fg(green) %s|@";
            default        -> "%s"; // No color for other levels
        };

        String formattedMessage = String.format(colorMarkup, "[%s] %s".formatted(level, message));

        // Render the markup using picocli and return the final string
        return Ansi.AUTO.string(formattedMessage) + "\n";
    }
}