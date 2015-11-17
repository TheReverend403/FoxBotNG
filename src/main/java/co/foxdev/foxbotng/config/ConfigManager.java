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
import co.foxdev.foxbotng.Main;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import lombok.Getter;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class ConfigManager {
    private FoxBotNG bot;
    private static final String CONFIG_BOTS_KEY = "bots";

    @Getter
    private Set<ClientConfig> clientConfigs = new HashSet<>();
    private File configPath;

    public ConfigManager(FoxBotNG bot) {
        this.bot = bot;
        configPath = null;
    }

    public ConfigManager(FoxBotNG bot, File configPath) {
        this.bot = bot;
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
            bot.getLogger().info("Config not found, saving a default config to " + configFile.getAbsolutePath());
            bot.getLogger().info("Please configure your bot before running it");

            InputStream stream = Main.class.getResourceAsStream(jarResource);
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
        bot.getLogger().info("Loading config");
        ConfigObject servers = conf.getObject(CONFIG_BOTS_KEY);
        for (String server : servers.keySet()) {
            ClientConfig clientConfig = new ClientConfig(conf.getConfig(CONFIG_BOTS_KEY + "." + server));
            clientConfigs.add(clientConfig);
        }
    }

    private File getConfigPath() throws IOException {
        if (configPath != null) {
            if (!configPath.exists()) {
                bot.getLogger().warn("Specified config file not found, falling back to default.");
            } else {
                return configPath;
            }
        }

        String configHome;
        File configDir;
        if ((configHome = System.getenv("XDG_CONFIG_HOME")) == null) {
            configHome = System.getProperty("user.home") + "/.config";
            bot.getLogger().debug("Could not detect config directory from XDG_CONFIG_HOME, using " + configHome);
        }

        configDir = new File(configHome, "/foxbot");
        configPath = new File(configDir, "/foxbot.conf");
        if (!configDir.exists() && !configDir.mkdir()) {
            throw new IOException("Could not create config directory.");
        }
        return configPath;
    }
}
