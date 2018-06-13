package org.owen.hermes.server.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.owen.hermes.config.Configuration;
import org.owen.hermes.model.Transport;
import org.owen.hermes.stub.SipServer;
import org.owen.hermes.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UdpSipServer extends SipServer {
    private Logger logger = LoggerFactory.getLogger(UdpSipServer.class);

    private final EventLoopGroup udpGroup = new NioEventLoopGroup();

    private Map<String, Object> configMap;

    private SipMessageHandler sipMessageHandler;
    private Transport transport = Transport.UDP;

    public UdpSipServer(SipMessageHandler sipMessageHandler) {
        this.sipMessageHandler = sipMessageHandler;
        // Set Netty channel initializer
        this.channelInitializer = new UdpChannelInitializer(this.sipMessageHandler);

        // Set transport configs
        this.configMap = Configuration.getInstance().getConfigMap(this.transport);
    }

    @Override
    public ChannelFuture run() throws Exception {
        /*
        try {
            // TODO: change to own Bootstrap
            final Bootstrap b = new Bootstrap();
            // TODO: add something needeed server options
//            b.group(udpGroup)
//                    .channel(NioDatagramChannel.class)
//                    .handler(this.channelInitializer);
            b.group(udpGroup)
                    .channel(NioDatagramChannel.class)
                    .handler(this.channelInitializer);

            ChannelFuture f = b.bind((int) configMap.get("port"));

            logger.info("Run UDP Server Listening on {}", configMap.get("port"));

//            f.channel().closeFuture();

            f.await();


            return f;
        } finally {
            if (logger.isDebugEnabled())
                logger.debug("Shut down UdpSipServer gracefully...");

            if (udpGroup != null) {
                udpGroup.shutdownGracefully();
            }
        }

        */

        // TODO: change to own Bootstrap
        final Bootstrap b = new Bootstrap();
        // TODO: add something needeed server options
//            b.group(udpGroup)
//                    .channel(NioDatagramChannel.class)
//                    .handler(this.channelInitializer);
        b.group(udpGroup)
                .channel(NioDatagramChannel.class)
                .handler(this.channelInitializer);

        ChannelFuture f = b.bind((int) configMap.get("port"));

        logger.info("Run UDP Server Listening on {}", configMap.get("port"));

        /*
        f.channel().closeFuture().addListener((event)->{
            System.out.println("event : ");
        });
        */

//        f.channel().closeFuture().sync();
        return f;
    }
}
