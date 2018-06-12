package org.lunker.new_proxy.server.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslContext;
import org.lunker.new_proxy.model.Transport;
import org.lunker.new_proxy.server.TransportChannelInitializer;
import org.lunker.new_proxy.sip.processor.SipPreProcessor;
import org.lunker.new_proxy.stub.SipMessageHandler;
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
        // byte -> str
        ch.pipeline().addLast("decoder", new TcpStreamDecoder());


        // PreProcessor도 생성해서 받아옴
        // str -> lb or proxy message
        // PreProcessor는 ServerInfo를 알아야함. .  . .
        ch.pipeline().addLast("preProcessor", new SipPreProcessor(transport)); // Lb, Proxy


        //org.lunker.proxy.sip.SipServletImpl.class;
        // SipMessageHandlerImpl 객체는 받아오고
        ch.pipeline().addLast("sipServletImpl", sipMessageHandler);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ExceptionCaught:: " + cause.getMessage());
    }
}
