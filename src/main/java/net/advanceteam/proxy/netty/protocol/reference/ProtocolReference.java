package net.advanceteam.proxy.netty.protocol.reference;

import io.netty.util.AttributeKey;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.connection.server.Server;

public final class ProtocolReference {

    public static final AttributeKey<Player> PLAYER_ATTRIBUTE_KEY = AttributeKey.newInstance("Player");
    public static final AttributeKey<Server> SERVER_ATTRIBUTE_KEY = AttributeKey.newInstance("Server");
}
