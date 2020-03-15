package net.advanceteam.proxy.common.event.impl;

import lombok.*;
import net.advanceteam.proxy.common.event.ProxyEvent;
import net.advanceteam.proxy.netty.protocol.client.packet.handshake.HandshakePacket;

import java.nio.channels.Channel;

@RequiredArgsConstructor
@Getter
public class PlayerHandshakeEvent extends ProxyEvent {

    private final Channel connection;
    private final HandshakePacket handshake;
}
