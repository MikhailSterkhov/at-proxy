package net.advanceteam.proxy.common.event.impl;

import io.netty.channel.Channel;
import lombok.*;
import net.advanceteam.proxy.common.event.ProxyEvent;
import net.advanceteam.proxy.common.ping.StatusResponseCallback;

@RequiredArgsConstructor
@Getter
public class ProxyPingEvent extends ProxyEvent {

    private final Channel connection;

    private final StatusResponseCallback response;
}
