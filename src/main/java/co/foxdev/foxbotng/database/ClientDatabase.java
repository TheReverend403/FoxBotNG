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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ClientDatabase {
    private static final FoxBotNG bot = FoxBotNG.getInstance();
    private final Client client;

    public ClientDatabase(Client client) {
        this.client = client;
        try {
            createIfNotExists(client);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void createIfNotExists(Client client) throws SQLException, IOException {
        String clientName = bot.getClientManager().getConfig(client).getClientName();
        bot.getLogger().info("Creating database for {}", clientName);
        Connection conn = getConnection();
        if (conn != null) {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE nick_ids (nick_id INTEGER PRIMARY KEY AUTOINCREMENT)");
            stmt.executeUpdate("CREATE TABLE nicknames (" +
                    "nick_id INTEGER REFERENCES nick_ids, slug STRING PRIMARY KEY, canonical string)");
            stmt.executeUpdate("CREATE TABLE nick_values (nick_id INTEGER REFERENCES nick_ids(nick_id), " +
                    "key STRING, value STRING, PRIMARY KEY (nick_id, key))");
            stmt.executeUpdate("CREATE TABLE channel_values (" +
                    "channel STRING, key STRING, value STRING, PRIMARY KEY (channel, key))");
            conn.close();
        }
    }

    private Connection getConnection() {
        Connection conn;
        try {
            conn = DriverManager.getConnection(getJDBCUrl());
        } catch (SQLException ex) {
            bot.getLogger().error("Error while getting DB connection for " +
                    bot.getClientManager().getConfig(client).getClientName(), ex);
            return null;
        }
        return conn;
    }

    private File getDatabasePath() {
        String clientName = bot.getClientManager().getConfig(client).getClientName();
        return new File(bot.getDatabaseManager().getDataDir(), clientName + ".db");
    }

    private String getJDBCUrl() {
        return String.format("jdbc:sqlite:%s", getDatabasePath().getAbsolutePath());
    }
}
