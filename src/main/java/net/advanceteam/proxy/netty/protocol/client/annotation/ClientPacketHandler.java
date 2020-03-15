package net.advanceteam.proxy.netty.protocol.client.annotation;

import jdk.nashorn.internal.objects.annotations.Getter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ClientPacketHandler {

    @Getter
    String packetQuery() default "null";
}
