package net.advanceteam.proxy.netty.protocol.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.common.chat.ChatColor;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.netty.protocol.handler.PacketHandlerAdapter;
import net.advanceteam.proxy.netty.protocol.packet.impl.game.DisconnectPacket;
import net.advanceteam.proxy.netty.protocol.reference.ProtocolReference;

public class ClientPacketHandler extends PacketHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext handlerContext) { }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        Attribute<Player> attribute = channelHandlerContext.channel().attr(ProtocolReference.PLAYER_ATTRIBUTE_KEY);

        if (attribute == null || attribute.get() == null) {
            return;
        }

        //channelHandlerContext.channel().writeAndFlush(new DisconnectPacket(ChatColor.GOLD + cause.getLocalizedMessage()));
        cause.printStackTrace();
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) {
        Attribute<Player> attribute = channelHandlerContext.channel().attr(ProtocolReference.PLAYER_ATTRIBUTE_KEY);

        if (attribute == null || attribute.get() == null) {
            return;
        }

        AdvanceProxy.getInstance().getPlayerManager().disconnectPlayer(attribute.get().getName());
    }

}
