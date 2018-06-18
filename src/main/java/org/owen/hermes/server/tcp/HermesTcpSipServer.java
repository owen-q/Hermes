package org.owen.hermes.server.tcp;

import io.netty.channel.ChannelOption;
import org.owen.hermes.bootstrap.ServerStarterElement;
import org.owen.hermes.bootstrap.handler.HermesAbstractSipHandler;
import org.owen.hermes.bootstrap.channel.HermesChannelInboundHandler;
import org.owen.hermes.model.Transport;
import org.owen.hermes.stub.SipServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.ipc.netty.tcp.BlockingNettyContext;
import reactor.ipc.netty.tcp.TcpServer;

/**
 * Created by dongqlee on 2018. 6. 13..
 */
public class HermesTcpSipServer extends SipServer{
    private Logger logger = LoggerFactory.getLogger(HermesTcpSipServer.class);

    private TcpServer reactorTcpServer = null;
//    private SSLContext sslContext = null;
    private HermesAbstractSipHandler serverHandler = null;

    //BiFunction<NettyInbound, NettyOutbound, ? extends Publisher<Void>>
    private HermesTcpSipServer(TcpServer reactorTcpServer, HermesAbstractSipHandler serverHandler) {
        this.reactorTcpServer = reactorTcpServer;
        this.serverHandler = serverHandler;
    }

    public static HermesTcpSipServer create(ServerStarterElement serverStarterElement){
        TcpServer reactorTcpServer =
                TcpServer.create(opts -> opts
                        .afterChannelInit((channel -> {
                            if(serverStarterElement.sslContext == null)
                                channel.pipeline().addFirst(Transport.TCP.getValue(), new HermesChannelInboundHandler());
                            else
                                channel.pipeline().addFirst(Transport.TLS.getValue(), new HermesChannelInboundHandler());

//                            channel.pipeline().addFirst(Transport.TCP.getValue(), new HermesMessageConverter());
                        }))
                        .host(serverStarterElement.serverListenHost)
                        .port(serverStarterElement.serverListenPort)
                        .option(ChannelOption.AUTO_READ,true) // turn on for handling SIP
                        .option(ChannelOption.SO_KEEPALIVE, false)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .option(ChannelOption.SO_LINGER, 0 )
                        .option(ChannelOption.SO_REUSEADDR, true)
                        .sslContext(serverStarterElement.sslContext)
                );

        return new HermesTcpSipServer(reactorTcpServer, serverStarterElement.hermesAbstractSipHandler);
    }

    public void close(){
        if(logger.isDebugEnabled())
            logger.debug("Stop server...");
//        this.connectedResult.dispose();
    }

    @Override
    public void run(boolean isSync) throws Exception {
        if(isSync){
            if(logger.isDebugEnabled())
                logger.debug("Start server as sync");

            // Make blocking server
            BlockingNettyContext blockingNettyContext = this.reactorTcpServer.start(this.serverHandler);

            blockingNettyContext.installShutdownHook();
            blockingNettyContext.getContext().onClose().block();
        }
        else {
            if(logger.isDebugEnabled())
                logger.debug("Start server as async");

            BlockingNettyContext blockingNettyContext = this.reactorTcpServer.start(this.serverHandler);

        }
    }
}