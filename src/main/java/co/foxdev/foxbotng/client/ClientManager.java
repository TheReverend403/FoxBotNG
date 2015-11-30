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

package co.foxdev.foxbotng.client;

import co.foxdev.foxbotng.config.ClientConfig;
import co.foxdev.foxbotng.listeners.MessageListener;
import co.foxdev.foxbotng.listeners.ServerListener;
import lombok.extern.slf4j.Slf4j;
import org.kitteh.irc.client.library.Client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ClientManager {
    private final Map<Client, ClientConfig> clientConfigs = new HashMap<>();

    /**
     * Gets a raw ClientConfig object to retrieve Client settings from.
     * @param client The Client to get settings for
     * @return The ClientConfig for the passed Client parameter
     */
    public ClientConfig getConfig(Client client) {
        if (clientConfigs.containsKey(client)) {
            return clientConfigs.get(client);
        }
        return null;
    }

    /**
     * Builds a KittehIRCClientLib client and connects it.
     * @param config ClientConfig this client gets its settings from
     * @return a connected Client
     */
    public Client buildClient(ClientConfig config) {
        log.info("Creating client for {}", config.getServerHost());

        Client client = Client.builder().nick(config.getBotNick())
                .user(config.getBotIdent())
                .realName(config.getBotRealname())
                .serverHost(config.getServerHost())
                .serverPort(config.getServerPort())
                .secure(config.isServerSsl())
                .serverPassword(config.getServerPassword()).build();

        clientConfigs.put(client, config);
        client.getEventManager().registerEventListener(new MessageListener());
        client.getEventManager().registerEventListener(new ServerListener());

        config.getChannels().forEach(client::addChannel);
        return client;
    }

    public Set<Client> getClients(){
        return clientConfigs.keySet();
    }

}
