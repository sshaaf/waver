package dev.shaaf.waver.cli;


import dev.shaaf.waver.llm.config.AppConfig;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * The main entry point for the CLI application.
 * Its primary role is to parse arguments, set up configuration,
 * and delegate the core logic to the Application class.
 */
@Command(name = "waver-cli") // All the command details are now in CliArguments
public class WaverCliCommand implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(WaverCliCommand.class.getName());

    @CommandLine.Mixin
    private CliArguments cliArguments;

    @Override
    public Integer call() {
        try {
            AppConfig appConfig = ConfigurationFactory.from(cliArguments);
            Application app = new Application(appConfig);
            return app.run();
        } catch (Exception e) {
            logger.severe(CommandLine.Help.Ansi.AUTO.string("@|fg(red),bold ERROR:|@ " + e.getMessage()));
            if (cliArguments != null && cliArguments.verbose) {
                e.printStackTrace();
            }
            return 1;
        }
    }
}
