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

package co.foxdev.foxbotng;

import co.foxdev.foxbotng.config.ConfigManager;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class FoxBotNG {
    @Getter
    private final Logger logger = LogManager.getLogger(FoxBotNG.class);
    @Getter
    private ConfigManager configManager = null;

    public FoxBotNG() {
        logger.info("Starting FoxBotNG " + Main.class.getPackage().getImplementationVersion());
        configManager = new ConfigManager(this);
        try {
            if (!configManager.initConfig()) {
                logger.error("Error loading config!");
            }
        } catch (IOException ex) {
            logger.error("Error loading config!", ex);
        }
    }
}
