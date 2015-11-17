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
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.user.PrivateCTCPQueryEvent;
import org.kitteh.irc.client.library.event.user.PrivateMessageEvent;
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler;

public class MessageListener {
    private FoxBotNG bot = null;

    public MessageListener(FoxBotNG bot) {
        this.bot = bot;
    }

    @Handler
    public void onChannelMessage(ChannelMessageEvent event) {
        Channel channel = event.getChannel();
        User actor = event.getActor();

        String logMessage = String.format("[%s] %s: <%s> %s",
                event.getClient().getServerInfo().getAddress().orElse("unknown"),
                channel.getName(),
                actor.getNick(),
                event.getMessage());
        bot.getLogger().info(logMessage);

        String url;
        if ((url = UrlExtractor.getFrom(event.getMessage())) != null) {
            String message = UrlExtractor.parseUrlForTitle(url);
            channel.sendMessage(String.format("[ %s ] - %s", message, url.replaceAll(".*://", "")));
        }
    }

    @Handler
    public void onPrivateMessage(PrivateMessageEvent event) {
        User actor = event.getActor();

        String logMessage = String.format("[%s] <%s> %s",
                event.getClient().getServerInfo().getAddress().orElse("unknown"),
                actor.getNick(),
                event.getMessage());
        bot.getLogger().info(logMessage);
    }

    @Handler
    public void onCtcp(PrivateCTCPQueryEvent event) {
        User actor = event.getActor();

        String logMessage = String.format("[%s] <%s> CTCP %s",
                event.getClient().getServerInfo().getAddress().orElse("unknown"),
                actor.getNick(),
                event.getMessage());
        bot.getLogger().info(logMessage);

        ClientConfig clientConfig = bot.getConfigManager().getClientConfig(event.getClient());
        if (clientConfig.getCtcpReplies().containsKey(event.getMessage().toLowerCase())) {
            event.setReply(clientConfig.getCtcpReplies().get(event.getMessage().toLowerCase()));
        }
    }
}
