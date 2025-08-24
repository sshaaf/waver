package dev.shaaf.waver.cli;

import dev.shaaf.waver.cli.log.LogConfig;
import picocli.CommandLine;

import java.util.logging.Logger;

/**
 * The main entry point of the application.
 * Its sole responsibility is to initialize logging and execute the CLI command.
 */
public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        LogConfig.setLoggingConfig(logger);
        int exitCode = new CommandLine(new WaverCliCommand()).execute(args);
        System.exit(exitCode);
    }
}
