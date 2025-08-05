package dev.shaaf.waver.cli.log;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class LogConfig {

    public final static void setLoggingConfig(Logger logger){
        // --- Configuration ---
        // Prevent logs from propagating to the root logger
        logger.setUseParentHandlers(false);

        // Create a console handler
        ConsoleHandler handler = new ConsoleHandler();

        // Set our custom formatter
        handler.setFormatter(new ColorFormatter());

        // Add the handler to the logger
        logger.addHandler(handler);
        // --- End Configuration ---
    }
}
