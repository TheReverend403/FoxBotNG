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
import org.kitteh.irc.client.library.element.Actor;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.*;
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler;

public class ChannelListener {
    private static final FoxBotNG bot = FoxBotNG.getInstance();

    @Handler
    public void onChannelJoin(ChannelJoinEvent event) {
        User actor = event.getActor();
        Channel channel = event.getChannel();

        String logMessage = String.format("[%s] JOIN %s %s",
                event.getClient().getServerInfo().getAddress().orElse("unknown"),
                channel.getName(),
                actor.getNick());
        bot.getLogger().info(logMessage);
    }

    @Handler
    public void onChannelPart(ChannelPartEvent event) {
        User actor = event.getActor();
        Channel channel = event.getChannel();

        String logMessage = String.format("[%s] PART %s %s (%s)",
                event.getClient().getServerInfo().getAddress().orElse("unknown"),
                channel.getName(),
                actor.getNick(),
                event.getMessage());
        bot.getLogger().info(logMessage);
    }

    @Handler
    public void onChannelKick(ChannelKickEvent event) {
        User actor = event.getActor();
        Channel channel = event.getChannel();

        String logMessage = String.format("[%s] KICK %s %s -> %s (%s)",
                event.getClient().getServerInfo().getAddress().orElse("unknown"),
                channel.getName(),
                actor.getNick(),
                event.getTarget().getNick(),
                event.getMessage());
        bot.getLogger().info(logMessage);
    }

    @Handler
    public void onChannelTopic(ChannelTopicEvent event) {
        Channel channel = event.getChannel();

        String logMessage = String.format("[%s] TOPIC %s %s",
                event.getClient().getServerInfo().getAddress().orElse("unknown"),
                channel.getName(),
                channel.getTopic().getValue().orElse("(none)"));
        bot.getLogger().info(logMessage);
    }

    @Handler
    public void onChannelMode(ChannelModeEvent event) {
        Actor actor = event.getActor();
        Channel channel = event.getChannel();

        String logMessage = String.format("[%s] MODE %s -> %s %s",
                event.getClient().getServerInfo().getAddress().orElse("unknown"),
                actor.getName(),
                channel.getName(),
                event.getStatusList().getStatusString());
        bot.getLogger().info(logMessage);
    }
}
