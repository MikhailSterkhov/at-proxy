package net.advanceteam.proxy.netty.protocol.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.netty.protocol.handler.PacketHandlerAdapter;
import net.advanceteam.proxy.netty.protocol.packet.MinecraftPacket;
import net.advanceteam.proxy.netty.protocol.packet.impl.UndefinedPacket;
import net.advanceteam.proxy.netty.protocol.reference.ProtocolReference;

public class ServerPacketHandler extends PacketHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext handlerContext) { }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) { }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MinecraftPacket clientPacket) {
        if (!(clientPacket instanceof UndefinedPacket)) {
            return;
        }

        Attribute<Player> attribute = ctx.channel().attr(ProtocolReference.PLAYER_ATTRIBUTE_KEY);

        if (attribute == null || attribute.get() == null) {
            return;
        }

        attribute.get().getServer().sendPacket(clientPacket);
    }

}
