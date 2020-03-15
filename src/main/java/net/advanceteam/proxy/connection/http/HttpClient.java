package net.advanceteam.proxy.connection.http;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.advanceteam.proxy.common.callback.Callback;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpClient {

    public static final int TIMEOUT = 5000;
    private static final Cache<String, InetAddress> addressCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();


    /**
     * Подключиться к URL
     *
     * @param url - url-адрес
     * @param eventLoop - поток
     * @param callback - callback
     */
    public static void connectToUrl(String url, EventLoop eventLoop, Callback<String> callback) {
        Preconditions.checkNotNull(url, "url");
        Preconditions.checkNotNull(eventLoop, "eventLoop");
        Preconditions.checkNotNull(callback, "callBack");

        final URI uri = URI.create(url);

        Preconditions.checkNotNull(uri.getScheme(), "scheme");
        Preconditions.checkNotNull(uri.getHost(), "host");

        boolean ssl = uri.getScheme().equals("https");
        int port = uri.getPort();

        if (port < 0) {
            switch (uri.getScheme()) {

                case "http":
                    port = 80;
                    break;

                case "https":
                    port = 443;
                    break;

                default:
                    throw new IllegalArgumentException("Unknown scheme " + uri.getScheme());
            }
        }

        InetAddress address = addressCache.getIfPresent(uri.getHost());

        try {

            if (address == null) {
                addressCache.put(uri.getHost(), (address = InetAddress.getByName(uri.getHost())));
            }

        } catch (UnknownHostException ex) {
            callback.done(null, ex);
            return;
        }

        ChannelFutureListener channelFutureListener = (future) -> {

            if (!future.isSuccess()) {
                addressCache.invalidate(uri.getHost());
                callback.done(null, future.cause());

                return;
            }

            String path = uri.getRawPath() + ((uri.getRawQuery() == null) ? "" : "?" + uri.getRawQuery());

            HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
            request.headers().set(HttpHeaderNames.HOST, uri.getHost());

            future.channel().writeAndFlush(request);
        };

        HttpInitializer httpInitializer = new HttpInitializer(callback, uri.getHost(), ssl, port);

        new Bootstrap().channel(NioSocketChannel.class)
                .group(eventLoop)
                .handler(httpInitializer)

                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
                .remoteAddress(address, port)

                .connect().addListener(channelFutureListener);
    }
}
