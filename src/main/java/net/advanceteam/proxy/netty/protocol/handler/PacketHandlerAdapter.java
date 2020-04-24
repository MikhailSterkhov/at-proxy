package net.advanceteam.proxy.netty.protocol.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;
import net.advanceteam.proxy.netty.protocol.packet.impl.UndefinedPacket;
import net.advanceteam.proxy.netty.protocol.reference.ProtocolReference;

public class PacketHandlerAdapter extends SimpleChannelInboundHandler<MinecraftPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MinecraftPacket minecraftPacket) {
        if (!(minecraftPacket instanceof UndefinedPacket)) {
            return;
        }

        Attribute<Player> attribute = ctx.channel().attr(ProtocolReference.PLAYER_ATTRIBUTE_KEY);

        if (attribute == null) {
            return;
        }

        attribute.get().sendPacket(minecraftPacket);
    }

}
