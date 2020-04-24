package net.advanceteam.proxy.netty.protocol.status;

import net.advanceteam.proxy.netty.protocol.direction.ProtocolDirection;
import net.advanceteam.proxy.netty.protocol.packet.impl.game.ChatPacket;
import net.advanceteam.proxy.netty.protocol.packet.impl.game.DisconnectPacket;
import net.advanceteam.proxy.netty.protocol.packet.impl.game.LoginPacket;
import net.advanceteam.proxy.netty.protocol.packet.impl.handshake.HandshakePacket;
import net.advanceteam.proxy.netty.protocol.packet.impl.login.*;
import net.advanceteam.proxy.netty.protocol.packet.impl.status.StatusPingPacket;
import net.advanceteam.proxy.netty.protocol.packet.impl.status.StatusRequestPacket;
import net.advanceteam.proxy.netty.protocol.packet.impl.status.StatusResponsePacket;
import net.advanceteam.proxy.netty.protocol.version.MinecraftVersion;

import java.util.HashMap;

public enum ProtocolStatus {

    HANDSHAKE {{
        ProtocolDirection.TO_SERVER.registerPacket(this, HandshakePacket::new, 0x00);
    }},

    STATUS {{
        ProtocolDirection.TO_SERVER.registerPacket(this, StatusRequestPacket::new, 0x00);
        ProtocolDirection.TO_SERVER.registerPacket(this, StatusPingPacket::new, 0x01);

        ProtocolDirection.TO_CLIENT.registerPacket(this, StatusResponsePacket::new, 0x00);
        ProtocolDirection.TO_CLIENT.registerPacket(this, StatusPingPacket::new, 0x01);
    }},

    LOGIN {{
        ProtocolDirection.TO_SERVER.registerPacket(this, LoginRequestPacket::new, 0x00);
        ProtocolDirection.TO_SERVER.registerPacket(this, EncryptionResponsePacket::new, 0x01);

        ProtocolDirection.TO_CLIENT.registerPacket(this, DisconnectPacket::new, 0x00);
        ProtocolDirection.TO_CLIENT.registerPacket(this, EncryptionRequestPacket::new, 0x01);
        ProtocolDirection.TO_CLIENT.registerPacket(this, LoginSuccessPacket::new, 0x02);
        ProtocolDirection.TO_CLIENT.registerPacket(this, SetCompressionPacket::new, 0x03);
    }},

    GAME {{
        ProtocolDirection.TO_CLIENT.registerPacket(this, DisconnectPacket::new, 0x00);
        ProtocolDirection.TO_CLIENT.registerPacket(this, ChatPacket::new, new HashMap<MinecraftVersion, Integer>() {{
            put(MinecraftVersion.V1_8, 0x02);
            put(MinecraftVersion.V1_9, 0x0F);
            put(MinecraftVersion.V1_9_1, 0x0F);
            put(MinecraftVersion.V1_9_2, 0x0F);
            put(MinecraftVersion.V1_9_3, 0x0F);
            put(MinecraftVersion.V1_9_4, 0x0F);
            put(MinecraftVersion.V1_10, 0x0F);
            put(MinecraftVersion.V1_11, 0x0F);
            put(MinecraftVersion.V1_11_1, 0x0F);
            put(MinecraftVersion.V1_11_2, 0x0F);
            put(MinecraftVersion.V1_12, 0x0F);
            put(MinecraftVersion.V1_12_1, 0x0F);
            put(MinecraftVersion.V1_12_2, 0x0F);
            put(MinecraftVersion.V1_13, 0x0E);
            put(MinecraftVersion.V1_13_1, 0x0E);
            put(MinecraftVersion.V1_13_2, 0x0E);
            put(MinecraftVersion.V1_14, 0x0E);
            put(MinecraftVersion.V1_14_1, 0x0E);
            put(MinecraftVersion.V1_14_2, 0x0E);
            put(MinecraftVersion.V1_14_3, 0x0E);
            put(MinecraftVersion.V1_14_4, 0x0E);
            put(MinecraftVersion.V1_15, 0x0F);
            put(MinecraftVersion.V1_15_1, 0x0F);
            put(MinecraftVersion.V1_15_2, 0x0F);
        }});
        ProtocolDirection.TO_CLIENT.registerPacket(this, LoginPacket::new, new HashMap<MinecraftVersion, Integer>() {{
            put(MinecraftVersion.V1_8, 0x01);
            put(MinecraftVersion.V1_9, 0x23);
            put(MinecraftVersion.V1_9_1, 0x23);
            put(MinecraftVersion.V1_9_2, 0x23);
            put(MinecraftVersion.V1_9_3, 0x23);
            put(MinecraftVersion.V1_9_4, 0x23);
            put(MinecraftVersion.V1_10, 0x23);
            put(MinecraftVersion.V1_11, 0x23);
            put(MinecraftVersion.V1_11_1, 0x23);
            put(MinecraftVersion.V1_11_2, 0x23);
            put(MinecraftVersion.V1_12, 0x23);
            put(MinecraftVersion.V1_12_1, 0x23);
            put(MinecraftVersion.V1_12_2, 0x23);
            put(MinecraftVersion.V1_13, 0x25);
            put(MinecraftVersion.V1_13_1, 0x25);
            put(MinecraftVersion.V1_13_2, 0x25);
            put(MinecraftVersion.V1_14, 0x25);
            put(MinecraftVersion.V1_14_1, 0x25);
            put(MinecraftVersion.V1_14_2, 0x25);
            put(MinecraftVersion.V1_14_3, 0x25);
            put(MinecraftVersion.V1_14_4, 0x25);
            put(MinecraftVersion.V1_15, 0x26);
            put(MinecraftVersion.V1_15_1, 0x26);
        }});
    }}

}
