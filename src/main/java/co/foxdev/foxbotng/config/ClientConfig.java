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
import com.typesafe.config.ConfigObject;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientConfig {
    @Getter
    private String nick;
    @Getter
    private String ident;
    @Getter
    private String realname;
    @Getter
    private String host;
    @Getter
    private int port;
    @Getter
    private boolean ssl;
    @Getter
    private List<String> channels;
    @Getter
    private Map<String, String> ctcpReplies = new HashMap<>();

    public ClientConfig(Config config) {
        this.nick = config.getString("nick");
        this.ident = config.getString("ident");
        this.realname = config.getString("realname");
        this.host = config.getString("host");
        this.port = config.getInt("port");
        this.ssl = config.getBoolean("ssl");
        this.channels = config.getStringList("channels");
        ConfigObject ctcpConfig = config.getObject("ctcp-replies");
        for (String ctcpReply : ctcpConfig.keySet()) {
            this.ctcpReplies.put(ctcpReply, ctcpConfig.get(ctcpReply).render());
        }
    }
}
