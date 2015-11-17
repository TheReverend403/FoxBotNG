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

import com.typesafe.config.Config;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientConfig {
    @Getter
    private String botNick;
    @Getter
    private String botIdent;
    @Getter
    private String botRealname;
    @Getter
    private Map<String, String> botCtcpReplies = new HashMap<>();

    @Getter
    private String serverHost;
    @Getter
    private int serverPort;
    @Getter
    private boolean serverSsl;
    @Getter
    private String serverPassword;
    @Getter
    private List<String> channels;

    /**
     * Builds a ClientConfig
     * @param config A TypeSafe Config library Config object to get settings from
     */
    public ClientConfig(Config config) {
        // Bot
        Config botConfig = config.getConfig("bot");
        this.botNick = botConfig.getString("nick");
        this.botIdent = botConfig.getString("ident");
        this.botRealname = botConfig.getString("realname");

        Config ctcpConfig = botConfig.getConfig("ctcp-replies");
        for (String ctcpReply : ctcpConfig.root().keySet()) {
            this.botCtcpReplies.put(ctcpReply, ctcpConfig.getString(ctcpReply));
        }

        // Server
        Config serverConfig = config.getConfig("server");
        this.serverHost = serverConfig.getString("host");
        this.serverPort = serverConfig.getInt("port");
        this.serverSsl = serverConfig.getBoolean("ssl");
        this.serverPassword = serverConfig.getString("password");
        this.channels = serverConfig.getStringList("channels");
    }
}
