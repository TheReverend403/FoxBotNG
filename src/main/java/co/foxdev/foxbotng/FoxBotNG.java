/*
 * This file is part of FoxBotNG.
 *
 * FoxBotNG is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBotNG is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package co.foxdev.foxbotng;

import co.foxdev.foxbotng.client.ClientManager;
import co.foxdev.foxbotng.config.ClientConfig;
import co.foxdev.foxbotng.config.ConfigManager;
import co.foxdev.foxbotng.database.DatabaseManager;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.io.File;
import java.io.IOException;

import static java.util.Arrays.asList;

public class FoxBotNG {
    @Getter
    private final Logger logger = LogManager.getLogger(FoxBotNG.class);
    @Getter
    private ConfigManager configManager;
    @Getter
    private ClientManager clientManager;
    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private static FoxBotNG instance;

    public static void main(final String[] args) {
        new FoxBotNG(args);
    }

    public FoxBotNG(String[] args) {
        instance = this;

        OptionParser parser = new OptionParser() {
            {
                acceptsAll(asList("h", "help", "?"),
                        "Prints this help screen.").forHelp();
                acceptsAll(asList("v", "verbose"),
                        "Enable debug for more verbose logging.");
                acceptsAll(asList("c", "config"),
                        "Specify an alternate config file.").withRequiredArg().ofType(File.class);
            }
        };
        OptionSet options = parser.parse(args);

        if (options.has("h")) {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        logger.info("Starting FoxBotNG {}", FoxBotNG.class.getPackage().getImplementationVersion());

        if (options.has("v")) {
            LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            Configuration config = ctx.getConfiguration();
            LoggerConfig loggerConfig = config.getLoggerConfig(logger.getName());
            loggerConfig.setLevel(Level.DEBUG);
            ctx.updateLoggers();
            logger.debug("Log level set to DEBUG");
        }

        if (options.has("c")) {
            File configFile = (File) options.valueOf("c");
            configManager = new ConfigManager(configFile);
        } else {
            configManager = new ConfigManager();
        }

        // Initialise config and create defaults if needed
        try {
            configManager.initConfig();
        } catch (IOException ex) {
            logger.error("Error while loading config", ex);
            return;
        }

        // Actually create and connect the bot(s)
        clientManager = new ClientManager();
        for (ClientConfig clientConfig : configManager.getClientConfigs()) {
            clientManager.buildClient(clientConfig);
        }

        databaseManager = new DatabaseManager();
        try {
            databaseManager.init();
        } catch (IOException ex) {
            logger.error("Error while creating DatabaseManager", ex);
        }
    }
}
