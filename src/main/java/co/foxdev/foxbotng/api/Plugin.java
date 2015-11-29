package co.foxdev.foxbotng.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zack6849 on 11/29/2015.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Plugin {
    String author() default "No Author Given";
    String name() default "No Name Set";
    String version() default "No Version Information";
    String description() default "No Description Provided";
}
