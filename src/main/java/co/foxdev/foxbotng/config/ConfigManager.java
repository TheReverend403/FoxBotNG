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
import co.foxdev.foxbotng.listeners.ChannelListener;
import co.foxdev.foxbotng.listeners.MessageListener;
import co.foxdev.foxbotng.listeners.ServerListener;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import org.kitteh.irc.client.library.Client;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private FoxBotNG bot = null;
    private Map<Client, ClientConfig> clientConfigs = new HashMap<>();

    public ConfigManager(FoxBotNG bot) {
        this.bot = bot;
    }

    public boolean initConfig() throws IOException {
        String jarResource = "/foxbot.conf.dist";

        String configHome;
        if ((configHome = System.getenv("XDG_CONFIG_DIR")) == null) {
            configHome = System.getProperty("user.home") + "/.config";
            bot.getLogger().debug("Could not detect config directory from XDG_CONFIG_DIR, using " + configHome);
        }

        File configDir = new File(configHome + "/foxbot");
        if (!configDir.exists() && !configDir.mkdir()) {
            bot.getLogger().error("Could not create config directory, exiting.");
            return false;
        }

        File configFile = new File(configDir, "/foxbot.conf");
        if (!configFile.exists()) {
            bot.getLogger().info("Saving a default config to " + configFile.getAbsolutePath());
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
            return false;
        }

        System.setProperty("config.file", configFile.getAbsolutePath());
        Config conf = ConfigFactory.load();
        bot.getLogger().info("Loading config");
        ConfigObject servers = conf.getObject("servers");
        for (String server : servers.keySet()) {
            ClientConfig clientConfig = new ClientConfig(conf.getConfig("servers." + server));
            clientConfigs.put(clientFromConfig(clientConfig), clientConfig);
        }
        return true;
    }

    public ClientConfig getClientConfig(Client client) {
        if (clientConfigs.containsKey(client)) {
            return clientConfigs.get(client);
        }
        return null;
    }

    private Client clientFromConfig(ClientConfig config) {
        bot.getLogger().info("Creating client for " + config.getHost());
        Client client = Client.builder().nick(config.getNick())
                .user(config.getIdent())
                .realName(config.getRealname())
                .serverHost(config.getHost())
                .serverPort(config.getPort())
                .secure(config.isSsl()).build();

        client.getEventManager().registerEventListener(new MessageListener(bot));
        client.getEventManager().registerEventListener(new ChannelListener(bot));
        client.getEventManager().registerEventListener(new ServerListener(bot));

        config.getChannels().forEach(client::addChannel);
        return client;
    }
}
