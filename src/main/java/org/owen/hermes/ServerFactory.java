package org.owen.hermes;

import io.netty.channel.ChannelFuture;
import org.owen.hermes.model.ServerInfo;
import org.owen.hermes.model.Transport;
import org.owen.hermes.server.tcp.TcpSipServer;
import org.owen.hermes.server.udp.UdpSipServer;
import org.owen.hermes.server.websocket.WebsocketSipServer;
import org.owen.hermes.stub.SipServer;
import org.owen.hermes.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Created by dongqlee on 2018. 6. 13..
 */
public class ServerFactory {
    private Logger logger = LoggerFactory.getLogger(ServerFactory.class);

    public static SipServer createServer(String host, int port, Transport transport){
        SipServer sipServer =null;

        if(Transport.TCP.equals(transport)){
            sipServer=new TcpSipServer(sipMessageHandler, false);
        }
        else if(Transport.UDP.equals(transport)){
            sipServer=new UdpSipServer(sipMessageHandler);
        }
        else if(Transport.TLS.equals(transport)){
            sipServer = new TcpSipServer(sipMessageHandler, true);
        }
        else if(Transport.WS.equals(transport)){
            sipServer=new WebsocketSipServer(sipMessageHandler, false);
        }
        else if(Transport.WSS.equals(transport)){
            sipServer=new WebsocketSipServer(sipMessageHandler, true);
        }

        return sipServer;
    }

    private static Mono<ChannelFuture> generateServerThread(ServerInfo serverInfo, SipMessageHandler sipMessageHandler){
        Mono<ChannelFuture> serverThread= Mono.fromCallable(()->{
            SipServer server=null;
            ChannelFuture channelFuture=null;

            if(logger.isDebugEnabled())
                logger.debug("[{}] Server starting ...", serverInfo.getTransport());

            // Create Server instance
            server= SipServer.create(serverInfo, sipMessageHandler);

            try{
                // Run server
                channelFuture=server.run();
                if(logger.isDebugEnabled())
                    logger.debug("[{}] Server started", serverInfo.getTransport());
            }
            catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException(String.format("[%s] Server starting error. cause, %s", serverInfo.getTransport()));
            }

            return channelFuture;
        });

        serverThread.subscribeOn(Schedulers.newElastic(String.format("%s-server", serverInfo.getTransport().getValue())));
        return serverThread;
    }


    public static SipServer create(ServerInfo serverInfo, SipMessageHandler sipMessageHandler){
        SipServer server=null;


    }
}
