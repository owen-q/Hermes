package org.lunker.new_proxy.client.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.lunker.new_proxy.config.Configuration;
import org.lunker.new_proxy.exception.InvalidConfigurationException;
import org.lunker.new_proxy.model.Constants;
import org.lunker.new_proxy.model.ServerInfo;
import org.lunker.new_proxy.model.Transport;
import org.lunker.new_proxy.server.tcp.TcpStreamDecoder;
import org.lunker.new_proxy.sip.processor.SipPreProcessor;
import org.lunker.new_proxy.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class TcpClient {
    private Logger logger = LoggerFactory.getLogger(TcpClient.class);

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private final Transport transport;
    private Configuration configuration;
    private Map<String, Object> transportConfigMap;
    private SipMessageHandler sipMessageHandler;

    private SslContext sslContext;

    public TcpClient(Class SipMessageHandlerImpl, boolean ssl) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, InvalidConfigurationException {
        this.configuration = Configuration.getInstance();
        this.sipMessageHandler = (SipMessageHandler) SipMessageHandlerImpl.getConstructor().newInstance();
        if (ssl) {
            this.transport = Transport.TLS;
            this.transportConfigMap = configuration.getConfigMap(this.transport);
            try {
                // TODO: tls
                this.sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            } catch (SSLException e) {
                e.printStackTrace();
            }
        } else {
            this.transport = Transport.TCP;
            transportConfigMap = configuration.getConfigMap(this.transport);
            this.sslContext = null;
        }


    }

    public ChannelFuture connect(String host, int port) throws Exception {
        Bootstrap bootstrap = new Bootstrap();

        Map<String, Object> tcpOptions=(Map<String, Object>)transportConfigMap.get("options");

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
//                .handler(new TcpClientInitializer(this.sipMessageHandler))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        if (sslContext != null) {
                            ch.pipeline().addLast(sslContext.newHandler(ch.alloc(), host, port));
                        }
                        ch.pipeline().addLast(new TcpStreamDecoder());
                        ch.pipeline().addLast(new SipPreProcessor(transport));
                        ch.pipeline().addLast(sipMessageHandler);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        logger.error(cause.getMessage());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, (int) tcpOptions.get("so_backlog"))
                .option(ChannelOption.SO_LINGER, (int) tcpOptions.get("so_linger"))
                .option(ChannelOption.TCP_NODELAY, (boolean) tcpOptions.get("tcp_nodelay"))
                .option(ChannelOption.SO_REUSEADDR, (boolean) tcpOptions.get("so_reuseaddr"))
                .option(ChannelOption.SO_RCVBUF, (int) tcpOptions.get("so_rcvbuf"))
                .option(ChannelOption.SO_SNDBUF, (int) tcpOptions.get("so_sndbuf"));

        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        logger.info("connected to {}:{} using TCP", host, port);
        return channelFuture;
    }
}
