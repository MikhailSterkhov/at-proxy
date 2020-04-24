package net.advanceteam.proxy.netty.protocol.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.netty.protocol.handler.PacketHandlerAdapter;
import net.advanceteam.proxy.netty.protocol.reference.ProtocolReference;

public class ClientPacketHandler extends PacketHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext handlerContext) { }

    //@Override //todo: мне нужно видеть все ошибка пока что
    //public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) { }

    @Override
    public void channelInactive(ChannelHandlerContext handlerContext) {
        Attribute<Player> attribute = handlerContext.channel().attr(ProtocolReference.PLAYER_ATTRIBUTE_KEY);

        if (attribute == null || attribute.get() == null) {
            return;
        }

        AdvanceProxy.getInstance().getPlayerManager().disconnectPlayer(attribute.get().getName());
    }

}
