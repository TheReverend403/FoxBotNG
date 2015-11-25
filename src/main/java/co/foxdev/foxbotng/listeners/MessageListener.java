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
import co.foxdev.foxbotng.config.ClientConfig;
import co.foxdev.foxbotng.utils.UrlExtractor;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.user.PrivateCTCPQueryEvent;
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler;

public class MessageListener {
    private static final FoxBotNG bot = FoxBotNG.getInstance();

    @Handler
    public void onChannelMessage(ChannelMessageEvent event) {
        Channel channel = event.getChannel();

        String url;
        if ((url = UrlExtractor.getFrom(event.getMessage())) != null) {
            String message = UrlExtractor.parseUrlForTitle(url);
            channel.sendMessage(String.format("[ %s ] - %s", message, url.replaceAll(".*://", "")));
        }
    }

    @Handler
    public void onCtcp(PrivateCTCPQueryEvent event) {
        ClientConfig clientConfig = bot.getClientManager().getConfig(event.getClient());
        if (clientConfig.getBotCtcpReplies().containsKey(event.getMessage().toLowerCase())) {
            event.setReply(clientConfig.getBotCtcpReplies().get(event.getMessage().toLowerCase()));
        }
    }
}
