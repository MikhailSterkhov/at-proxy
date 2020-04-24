package net.advanceteam.proxy.netty.protocol.packet.info;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;
import net.advanceteam.proxy.netty.protocol.status.ProtocolStatus;

@Getter
@RequiredArgsConstructor
public class MinecraftPacketInfo {

    private final int packetId;

    private final Class<? extends MinecraftPacket> packetClass;

    private final MinecraftPacket packetInstance;
    private final ProtocolStatus protocolStatus;
}
