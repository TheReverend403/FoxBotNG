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

import co.foxdev.foxbotng.listeners.ChannelListener;
import co.foxdev.foxbotng.listeners.MessageListener;
import co.foxdev.foxbotng.listeners.ServerListener;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitteh.irc.client.library.Client;

import java.io.*;
import java.util.List;

public class FoxBotNG {
    @Getter
    private final Logger logger = LogManager.getLogger(FoxBotNG.class);

    public FoxBotNG() {
        logger.info("Starting FoxBotNG " + Main.class.getPackage().getImplementationVersion());

        try {
            if (!saveDefaultConfig()) {
                return;
            }
        } catch (IOException ex) {
            logger.error("Error saving default config", ex);
            return;
        }

        initClients();
    }

    private void initClients() {
        Config conf = ConfigFactory.load();
        logger.info("Loading config");
        ConfigObject servers = conf.getObject("servers");

        for (Object server : servers.unwrapped().keySet()) {
            final String nick = conf.getString("servers." + server + ".nick");
            final String ident = conf.getString("servers." + server + ".ident");
            final String realname = conf.getString("servers." + server + ".realname");
            final String host = conf.getString("servers." + server + ".host");
            final int port = conf.getInt("servers." + server + ".port");
            final boolean ssl = conf.getBoolean("servers." + server + ".ssl");
            final List<String> channels = conf.getStringList("servers." + server + ".channels");

            logger.info("Creating client for " + host);
            Client client = Client.builder().nick(nick).user(ident).realName(realname)
                    .serverHost(host)
                    .serverPort(port)
                    .secure(ssl).build();

            client.getEventManager().registerEventListener(new MessageListener(this));
            client.getEventManager().registerEventListener(new ChannelListener(this));
            client.getEventManager().registerEventListener(new ServerListener(this));

            for (String channel : channels) {
                client.addChannel(channel);
            }
        }
    }

    private boolean saveDefaultConfig() throws IOException {
        String jarResource = "/foxbot.conf.dist";

        String configHome;
        if ((configHome = System.getenv("XDG_CONFIG_DIR")) == null) {
            configHome = System.getProperty("user.home") + "/.config";
            logger.debug("Could not detect config directory from XDG_CONFIG_DIR, using " + configHome);
        }

        File configDir = new File(configHome + "/foxbot");
        if (!configDir.exists() && !configDir.mkdir()) {
            logger.error("Could not create config directory, exiting.");
            return false;
        }

        File configFile = new File(configDir, "/foxbot.conf");
        if (!configFile.exists()) {
            logger.info("Saving a default config to " + configFile.getAbsolutePath());
            logger.info("Please configure your bot before running it");

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
        return true;
    }
}
