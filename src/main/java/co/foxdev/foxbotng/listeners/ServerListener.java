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

package co.foxdev.foxbotng.listeners;

import co.foxdev.foxbotng.FoxBotNG;
import org.kitteh.irc.client.library.event.client.ClientReceiveMOTDEvent;
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler;

public class ServerListener {
    private static final FoxBotNG bot = FoxBotNG.getInstance();

    @Handler
    public void onServerMotd(ClientReceiveMOTDEvent event) {
        if (event.getMOTD().isPresent()) {
            for (String motdLine : event.getMOTD().get()) {
                bot.getLogger().info(motdLine);
            }
        }
    }
}
