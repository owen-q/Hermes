package org.lunker.new_proxy.server.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import org.lunker.new_proxy.server.TransportChannelInitializer;
import org.lunker.new_proxy.sip.processor.ServerProcessor;
import org.lunker.new_proxy.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 5. 29..
 */
public class WebsocketChannelInitializer extends TransportChannelInitializer {
    private Logger logger= LoggerFactory.getLogger(WebsocketChannelInitializer.class);
    private static final String WEBSOCKET_PATH="/";
    private final SslContext sslCtx;
    private SipMessageHandler sipMessageHandler;

    public WebsocketChannelInitializer(SipMessageHandler sipMessageHandler) {
        this.sipMessageHandler = sipMessageHandler;
        this.sslCtx = null;
    }

    public WebsocketChannelInitializer(SipMessageHandler sipMessageHandler, SslContext sslCtx) {
        this.sipMessageHandler = sipMessageHandler;
        this.sslCtx=sslCtx;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        // Websocket
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, "sip", true));
        pipeline.addLast(new WebSocketIndexPageHandler(WEBSOCKET_PATH));

        // Websocket decoder
        pipeline.addLast(new WebSocketFrameHandler());

        pipeline.addLast("preProcessor", sipMessageHandler);
    }
}
