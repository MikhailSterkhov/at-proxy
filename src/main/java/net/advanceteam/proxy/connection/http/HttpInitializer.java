package net.advanceteam.proxy.connection.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.RequiredArgsConstructor;
import net.advanceteam.proxy.common.callback.Callback;

import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class HttpInitializer extends ChannelInitializer<Channel> {

    private final Callback<String> callback;
    private final String host;

    private final boolean ssl;

    private final int port;

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("timeout", new ReadTimeoutHandler(
                HttpClient.TIMEOUT, TimeUnit.MILLISECONDS));

        if (ssl) {
            SSLEngine engine = SslContextBuilder.forClient().build().newEngine(channel.alloc(), host, port);

            pipeline.addLast("ssl", new SslHandler(engine));
        }

        pipeline.addLast("http", new HttpClientCodec());
        pipeline.addLast("handler", new HttpHandler(callback));
    }
}
