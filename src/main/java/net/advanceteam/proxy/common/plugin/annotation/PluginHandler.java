package net.advanceteam.proxy.common.plugin.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PluginHandler {
    
    String name() default "Unknown";
    String author() default "AdvanceTeam";
    String version() default "1.0";

}
