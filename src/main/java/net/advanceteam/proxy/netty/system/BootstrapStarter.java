package net.advanceteam.proxy.netty.system;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.netty.protocol.client.codec.ClientPacketDecoder;
import net.advanceteam.proxy.netty.protocol.client.codec.ClientPacketEncoder;
import net.advanceteam.proxy.netty.protocol.client.codec.frame.Varint21FrameDecoder;
import net.advanceteam.proxy.netty.protocol.client.codec.frame.Varint21LengthFieldEncoder;
import net.advanceteam.proxy.netty.protocol.client.handler.ClientHandlerFactory;

import java.net.InetSocketAddress;

public final class BootstrapStarter {


    /**
     * Бинд сервера по его адресу
     *
     * @param inetSocketAddress - адрес сервера
     * @param channelFutureListener - листенер
     */
    public void bindServer(InetSocketAddress inetSocketAddress, ChannelFutureListener channelFutureListener) {
        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) {
                ChannelPipeline channelPipeline = channel.pipeline();

                channelPipeline.addLast("frame-decoder", new Varint21FrameDecoder());
                channelPipeline.addLast("frame-prepender", new Varint21LengthFieldEncoder());

                channelPipeline.addAfter("frame-decoder", "packet-decoder", new ClientPacketDecoder());
                channelPipeline.addAfter("frame-prepender", "packet-encoder", new ClientPacketEncoder());

                channelPipeline.addLast("handler-factory", new ClientHandlerFactory());
            }
        };

        new ServerBootstrap().channel(NioServerSocketChannel.class)

                .group(AdvanceProxy.getInstance().getEventLoops())
                .childAttr(AttributeKey.valueOf("InetSocketAddress"), inetSocketAddress)
                .childHandler(channelInitializer)

                .option(ChannelOption.SO_REUSEADDR, true)

                .localAddress(inetSocketAddress)

                .bind().addListener(channelFutureListener);

    }

    /**
     * Подключение прокси к серверу по его адресу
     *
     * @param eventLoopGroup - поток
     * @param inetSocketAddress - адрес
     * @param channelFutureListener - листенер, выдающий результат
     */
    public void connectServer(EventLoopGroup eventLoopGroup, InetSocketAddress inetSocketAddress, ChannelFutureListener channelFutureListener) {
        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) {
                ChannelPipeline channelPipeline = channel.pipeline();

                channelPipeline.addLast("frame-decoder", new Varint21FrameDecoder());
                channelPipeline.addLast("frame-prepender", new Varint21LengthFieldEncoder());

                channelPipeline.addAfter("frame-decoder", "packet-decoder", new ClientPacketDecoder());
                channelPipeline.addAfter("frame-prepender", "packet-encoder", new ClientPacketEncoder());

                channelPipeline.addLast("handler-factory", new ClientHandlerFactory());
            }
        };

        new Bootstrap().channel( NioSocketChannel.class )

                .group( eventLoopGroup )
                .handler( channelInitializer )

                .option( ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000 )

                .remoteAddress( inetSocketAddress )
                .connect().addListener( channelFutureListener );
    }

}
