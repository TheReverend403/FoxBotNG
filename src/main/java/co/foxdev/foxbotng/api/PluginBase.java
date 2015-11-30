package co.foxdev.foxbotng.api;

import co.foxdev.foxbotng.FoxBotNG;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zack6849 on 11/29/2015.
 */

@Slf4j
public abstract class PluginBase {

    public abstract void onEnable();
    public abstract void onDisable();

    private Logger logger;

    public Logger getLogger(){
        if(logger == null){
            Plugin plugin = this.getClass().getAnnotation(Plugin.class);
            if(plugin == null){
                throw new IllegalArgumentException("Cannot call getLogger() on non-plugin annotated class.");
            }
            return LoggerFactory.getLogger(plugin.name());
        }else {
            return logger;
        }
    }

    public FoxBotNG getBot(){
        return FoxBotNG.getInstance();
    }
}
