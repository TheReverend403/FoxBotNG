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

package co.foxdev.foxbotng.database;

import co.foxdev.foxbotng.FoxBotNG;
import org.kitteh.irc.client.library.Client;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    private static final FoxBotNG bot = FoxBotNG.getInstance();
    private File dataDir;
    private final Map<Client, ClientDatabase> clientDatabases = new HashMap<>();

    public DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            bot.getLogger().error("Error while loading SQLite", ex);
        }
    }

    /**
     * Initialises directories for databases
     * @throws IOException
     */
    public void init() throws IOException {
        if (dataDir == null) {
            dataDir = new File(bot.getConfigManager().getConfigDir().getAbsolutePath(), "data");
        }
        if (!dataDir.exists()) {
            if (!dataDir.mkdir()) {
                throw new IOException("Couldn't create data directory");
            }
        }
    }

    /**
     * Gets the ClientDatabase object associated with a Client
     * @param client Client to get ClientDatabase for
     * @return ClientDatabase for Client
     */
    public ClientDatabase getDatabaseForClient(Client client) {
        if (clientDatabases.containsKey(client)) {
            return clientDatabases.get(client);
        }
        ClientDatabase clientDatabase = new ClientDatabase(client);
        clientDatabases.put(client, clientDatabase);
        return clientDatabase;
    }

    public File getDataDir() {
        return this.dataDir;
    }

    public Map<Client, ClientDatabase> getClientDatabases() {
        return this.clientDatabases;
    }
}
