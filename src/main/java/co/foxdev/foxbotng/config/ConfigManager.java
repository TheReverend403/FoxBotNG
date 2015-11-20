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

package co.foxdev.foxbotng.config;

import co.foxdev.foxbotng.FoxBotNG;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;
import org.slf4j.Logger;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class ConfigManager {
    private final Logger logger;
    private static final String CONFIG_BOTS_KEY = "bots";

    @Getter
    private Set<ClientConfig> clientConfigs = new HashSet<>();
    @Getter
    private File configDir;
    private File configPath;

    public ConfigManager() {
        this(null);
    }

    public ConfigManager(File configPath) {
        this.logger = FoxBotNG.getInstance().getLogger();
        this.configPath = configPath;
    }

    /**
     * Creates a default config file if none exists and creates ClientConfig objects from a map found in the config file.
     * @throws IOException
     */
    public void initConfig() throws IOException {
        String jarResource = "/foxbot.conf.dist";

        File configFile = getConfigPath();
        if (!configFile.exists()) {
            logger.info("Config not found, saving a default config to " + configFile.getAbsolutePath());
            logger.info("Please configure your bot before running it");

            InputStream stream = FoxBotNG.class.getResourceAsStream(jarResource);
            OutputStream resStreamOut;
            int readBytes;
            byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(configFile);

            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }

            stream.close();
            resStreamOut.close();
            // The bot hasn't been configured yet, so shut down to give the user a chance to do so.
            System.exit(0);
        }

        System.setProperty("config.file", configFile.getAbsolutePath());
        Config conf = ConfigFactory.load();
        logger.info("Loading config");
        Config servers = conf.getConfig(CONFIG_BOTS_KEY);
        for (String server : servers.root().keySet()) {
            ClientConfig clientConfig = new ClientConfig(server, servers.getConfig(server));
            clientConfigs.add(clientConfig);
            logger.debug("Config for bot '{}': {}", server, servers.getConfig(server));
        }
    }

    private File getConfigPath() throws IOException {
        if (configPath != null) {
            if (!configPath.exists()) {
                logger.warn("Specified config file ({}) not found, falling back to default.",
                        configPath.getAbsolutePath());
            } else {
                return configPath;
            }
        }

        String configHome;
        if ((configHome = System.getenv("XDG_CONFIG_HOME")) == null) {
            configHome = System.getProperty("user.home") + "/.config";
            logger.debug("Could not detect config directory from XDG_CONFIG_HOME, using {}", configHome);
        }

        configDir = new File(configHome, "/foxbot");
        configPath = new File(configDir, "/foxbot.conf");
        if (!configDir.exists() && !configDir.mkdir()) {
            throw new IOException("Could not create config directory.");
        }
        return configPath;
    }
}
