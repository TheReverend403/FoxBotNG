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

package co.foxdev.foxbotng.api;

import co.foxdev.foxbotng.FoxBotNG;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PluginBase {

    public abstract void onEnable();
    public abstract void onDisable();

    private Logger log;

    public Logger getLogger(){
        if (log == null) {
            Plugin plugin = this.getClass().getAnnotation(Plugin.class);
            if(plugin == null){
                throw new IllegalArgumentException("Cannot call getLogger() on non-plugin annotated class.");
            }
            return LoggerFactory.getLogger(plugin.name());
        }else {
            return log;
        }
    }

    public FoxBotNG getBot(){
        return FoxBotNG.getInstance();
    }
}
