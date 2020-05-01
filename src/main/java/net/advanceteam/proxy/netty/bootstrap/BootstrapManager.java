package net.advanceteam.proxy.netty.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import net.advanceteam.proxy.AdvanceProxy;
import net.advanceteam.proxy.connection.player.Player;
import net.advanceteam.proxy.connection.server.Server;
import net.advanceteam.proxy.netty.protocol.codec.MinecraftPacketDecoder;
import net.advanceteam.proxy.netty.protocol.codec.MinecraftPacketEncoder;
import net.advanceteam.proxy.netty.protocol.codec.frame.Varint21FrameDecoder;
import net.advanceteam.proxy.netty.protocol.codec.frame.Varint21LengthFieldEncoder;
import net.advanceteam.proxy.netty.protocol.handler.impl.ClientPacketHandler;
import net.advanceteam.proxy.netty.protocol.handler.impl.ServerPacketHandler;
import net.advanceteam.proxy.netty.protocol.reference.ProtocolReference;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class BootstrapManager {

    /**
     * Бинд сервера по его адресу
     *
     * @param inetSocketAddress - адрес сервера
     * @param channelFutureListener - листенер
     */
    public void bindServer(InetSocketAddress inetSocketAddress, ChannelFutureListener channelFutureListener) {
        new ServerBootstrap().channel(NioServerSocketChannel.class)
                .group(AdvanceProxy.getInstance().getEventLoops())

                .childAttr(AttributeKey.valueOf("InetSocketAddress"), inetSocketAddress)
                .childHandler(getServerChannelInitializer(null, ClientPacketHandler::new))

                .option(ChannelOption.SO_REUSEADDR, true)
                .localAddress(inetSocketAddress)

                .bind().addListener(channelFutureListener);
    }

    /**
     * Подключение прокси к серверу по его адресу
     *
     * @param eventLoopGroup - поток
     * @param channelFutureListener - листенер, выдающий результат
     */
    public void connectPlayerToServer(Player player, EventLoopGroup eventLoopGroup, InetSocketAddress inetSocketAddress, ChannelFutureListener channelFutureListener) {
        new Bootstrap().channel(NioSocketChannel.class)
                .handler(getPlayerChannelInitializer(channel -> channel.attr(ProtocolReference.PLAYER_ATTRIBUTE_KEY).set(player), ClientPacketHandler::new))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)

                .remoteAddress(inetSocketAddress)
                .group(eventLoopGroup)

                .connect().addListener(channelFutureListener);
    }

    /**
     * Подключение прокси к серверу по его адресу
     *
     * @param eventLoopGroup - поток
     * @param channelFutureListener - листенер, выдающий результат
     */
    public void connectServerToProxy(Server server, EventLoopGroup eventLoopGroup, ChannelFutureListener channelFutureListener) {
        new Bootstrap().channel(NioSocketChannel.class)
                .handler(getServerChannelInitializer(channel -> channel.attr(ProtocolReference.SERVER_ATTRIBUTE_KEY).set(server), ServerPacketHandler::new))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)

                .remoteAddress(server.getInetAddress())
                .group(eventLoopGroup)

                .connect().addListener(channelFutureListener);
    }

    /**
     * Создать и получить инитиалайзер канала для игрока
     *
     * @param preCommand - команда, которая будет выполняться перед регистрацией кодеков
     * @param handler - регистрируемый хандлер
     */
    private ChannelInitializer<Channel> getPlayerChannelInitializer(Consumer<Channel> preCommand, Supplier<ChannelHandler> handler) {
        return new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) {
                if (preCommand != null) {
                    preCommand.accept(channel);
                }

                ChannelPipeline channelPipeline = channel.pipeline();

                try {
                    channel.config().setOption(ChannelOption.IP_TOS, 0x18);
                }
                catch (ChannelException ex) {
                    // IP_TOS is not supported (Windows XP / Windows Server 2003)
                }

                channel.config().setAllocator(PooledByteBufAllocator.DEFAULT);
                // channel.config().setWriteBufferWaterMark( MARK );

                channelPipeline.addLast("timeout", new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));

                channelPipeline.addLast("frame-decoder", new Varint21FrameDecoder() );
                channelPipeline.addLast("frame-prepender", new Varint21LengthFieldEncoder());

                channelPipeline.addLast("inbound-boss", handler.get());

                channelPipeline.addAfter("frame-decoder", "packet-decoder", new MinecraftPacketDecoder());
                channelPipeline.addAfter("frame-prepender", "packet-encoder", new MinecraftPacketEncoder());
            }
        };
    }

    /**
     * Создать и получить инитиалайзер канала для сервера
     *
     * @param preCommand - команда, которая будет выполняться перед регистрацией кодеков
     * @param handler - регистрируемый хандлер
     */
    private ChannelInitializer<Channel> getServerChannelInitializer(Consumer<Channel> preCommand, Supplier<ChannelHandler> handler) {
        return new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) {
                if (preCommand != null) {
                    preCommand.accept(channel);
                }

                ChannelPipeline channelPipeline = channel.pipeline();

                channelPipeline.addLast("frame-decoder", new Varint21FrameDecoder() );
                channelPipeline.addLast("frame-prepender", new Varint21LengthFieldEncoder());

                channelPipeline.addLast("handler", handler.get());

                channelPipeline.addAfter("frame-decoder", "packet-decoder", new MinecraftPacketDecoder());
                channelPipeline.addAfter("frame-prepender", "packet-encoder", new MinecraftPacketEncoder());
            }
        };
    }

}
