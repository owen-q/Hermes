package org.lunker.new_proxy.server.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.lunker.new_proxy.config.Configuration;
import org.lunker.new_proxy.model.Transport;
import org.lunker.new_proxy.sip.processor.ServerProcessor;
import org.lunker.new_proxy.stub.AbstractServer;
import org.lunker.new_proxy.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UdpServer extends AbstractServer {
    private Logger logger = LoggerFactory.getLogger(UdpServer.class);

    private final EventLoopGroup udpGroup = new NioEventLoopGroup();

    private Map<String, Object> configMap;

    private SipMessageHandler sipMessageHandler;
    private Transport transport = Transport.UDP;

    public UdpServer(SipMessageHandler sipMessageHandler) {
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
                logger.debug("Shut down UdpServer gracefully...");

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
