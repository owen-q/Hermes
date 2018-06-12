package org.lunker.new_proxy.server.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {
    private Logger logger= LoggerFactory.getLogger(HttpChannelInitializer.class);
    private final SslContext sslContext;

    public HttpChannelInitializer(SslContext sslContext) {
        logger.info("create!!!!!!!!!!!!!!!!!!!!!!!!");
        this.sslContext=sslContext;
    }

    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(sslContext.newHandler(socketChannel.alloc()));
        pipeline.addLast("httpServerCodec", new HttpServerCodec());
        pipeline.addLast("httpHandler", new HttpServerHandler());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught:: " + cause.getMessage());
    }
}
