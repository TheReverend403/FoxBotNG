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
 * along with FoxBotNG.  If not, see <http://www.gnu.org/licenses/>.
 */

package co.foxdev.foxbotng;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import co.foxdev.foxbotng.api.PluginManager;
import co.foxdev.foxbotng.client.ClientManager;
import co.foxdev.foxbotng.config.ClientConfig;
import co.foxdev.foxbotng.config.ConfigManager;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static java.util.Arrays.asList;

@Slf4j
public final class FoxBotNG {
    // Constants for short versions of jopt args
    private static final String ARG_SHORT_HELP = "h";
    private static final String ARG_SHORT_VERBOSE = "v";
    private static final String ARG_SHORT_CONFIG = "c";
    @Getter
    private static FoxBotNG instance;
    @Getter
    private PluginManager pluginManager;
    @Getter
    private ConfigManager configManager;
    @Getter
    private ClientManager clientManager;

    private FoxBotNG() {}

    private void init(String[] args) {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(asList(ARG_SHORT_HELP, "help", "?"), "Prints this help screen.").forHelp();
        parser.acceptsAll(asList(ARG_SHORT_VERBOSE, "verbose"), "Enable debug for more verbose logging.");
        parser.acceptsAll(asList(ARG_SHORT_CONFIG, "config"), "Specify an alternate config file.")
                .withRequiredArg().ofType(File.class);
        OptionSet options = parser.parse(args);

        if (options.has(ARG_SHORT_HELP)) {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (options.has(ARG_SHORT_VERBOSE)) {
            Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            root.setLevel(Level.DEBUG);
            log.debug("Log level set to DEBUG");
        }

        if (options.has(ARG_SHORT_CONFIG)) {
            File configFile = (File) options.valueOf("c");
            configManager = new ConfigManager(configFile);
        } else {
            configManager = new ConfigManager();
        }
        // Initialise config and create defaults if needed
        try {
            configManager.initConfig();
        } catch (IOException ex) {
            log.error("Error while loading config", ex);
            return;
        }

        // Actually create and connect the bot(s)
        clientManager = new ClientManager();
        for (ClientConfig clientConfig : configManager.getClientConfigs()) {
            clientManager.buildClient(clientConfig);
        }

        try {
            pluginManager = new PluginManager();
        } catch (Exception ex) {
            log.error("Error while loading PluginManager", ex);
        }
    }

    // Inspired by https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/Bukkit.java#72
    private static void setInstance(FoxBotNG instance) {
        if (FoxBotNG.instance != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton FoxBotNG");
        }
        FoxBotNG.instance = instance;
        log.info("Starting {} {}", "FoxBotNG", instance.getClass().getPackage().getImplementationVersion());
    }

    public static void main(final String[] args) {
        setInstance(new FoxBotNG());
        instance.init(args);
    }
}
