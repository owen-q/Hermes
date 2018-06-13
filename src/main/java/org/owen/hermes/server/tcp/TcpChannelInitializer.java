package org.owen.hermes.server.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslContext;
import org.owen.hermes.model.Transport;
import org.owen.hermes.server.TransportChannelInitializer;
import org.owen.hermes.sip.processor.SipConverter;
import org.owen.hermes.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class TcpChannelInitializer extends TransportChannelInitializer {
    private Logger logger= LoggerFactory.getLogger(TcpChannelInitializer.class);
    private Transport transport=Transport.TCP;
    private SipMessageHandler sipMessageHandler=null;
    private final SslContext sslContext;

    public TcpChannelInitializer(SipMessageHandler sipMessageHandler) {
        this.sipMessageHandler = sipMessageHandler;
        this.sslContext = null;
    }

    public TcpChannelInitializer(SipMessageHandler sipMessageHandler, SslContext sslContext) {
        this.sipMessageHandler=sipMessageHandler;
        this.sslContext = sslContext;
        // TODO: tls
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        if (this.sslContext != null) {
            ch.pipeline().addLast(this.sslContext.newHandler(ch.alloc()));
        }

        ch.pipeline().addLast("decoder", new TcpStreamDecoder());
        ch.pipeline().addLast("preProcessor", new SipConverter(transport)); // Lb, Proxy

        ch.pipeline().addLast("sipServletImpl", sipMessageHandler);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        logger.error("ExceptionCaught:: " + cause.getMessage());
    }
}
