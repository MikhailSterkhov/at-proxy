package net.advanceteam.proxy.netty.protocol.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandlerFactory extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext handlerContext) { }

    @Override
    public void channelInactive(ChannelHandlerContext handlerContext) { }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) { }

}
