package org.owen.hermes.server.websocket;

import lombok.extern.slf4j.Slf4j;

import org.owen.hermes.bootstrap.ServerStarterElement;
import org.owen.hermes.bootstrap.channel.HermesChannelInboundHandler;
import org.owen.hermes.bootstrap.handler.HermesAbstractSipHandler;
import org.owen.hermes.model.Transport;
import org.owen.hermes.stub.SipServer;

import reactor.ipc.netty.http.server.HttpServer;
import reactor.ipc.netty.tcp.BlockingNettyContext;

/**
 * Created by owen_q on 2018. 6. 17..
 */
@Slf4j
public class HermesWebsocketSipServer extends SipServer{
    private HttpServer reactorNettyHttpServer = null;
    private HermesAbstractSipHandler hermesAbstractSipHandler = null;

    private HermesWebsocketSipServer(HttpServer reactorNettyHttpServer, HermesAbstractSipHandler hermesAbstractSipHandler) {
        this.reactorNettyHttpServer = reactorNettyHttpServer;
        this.hermesAbstractSipHandler = hermesAbstractSipHandler;
    }

    public static HermesWebsocketSipServer create(ServerStarterElement serverStarterElement){
        HttpServer httpServer;

        httpServer = HttpServer.create(
                opts -> opts
                        .afterChannelInit(channel -> {
                            if(serverStarterElement.sslContext == null)
                                channel.pipeline().addFirst(Transport.WS.getValue(), new HermesChannelInboundHandler());
                            else
                                channel.pipeline().addFirst(Transport.WSS.getValue(), new HermesChannelInboundHandler());
                        })
                        .host(serverStarterElement.serverListenHost)
                        .port(serverStarterElement.serverListenPort)
                        .sslContext(serverStarterElement.sslContext)
        );

        return new HermesWebsocketSipServer(httpServer, serverStarterElement.hermesAbstractSipHandler);
    }

    // TODO: Refactoring duplicated logic for starting server
    @Override
    public void run(boolean isSync) throws Exception {
        if(isSync){
            if(log.isDebugEnabled())
                log.debug("Start server as sync");

            // Make blocking server
            BlockingNettyContext blockingNettyContext = this.reactorNettyHttpServer.start(this.hermesAbstractSipHandler);

            blockingNettyContext.installShutdownHook();
            blockingNettyContext.getContext().onClose().block();
        }
        else {
            if(log.isDebugEnabled())
                log.debug("Start server as async");

            BlockingNettyContext blockingNettyContext = this.reactorNettyHttpServer.start(this.hermesAbstractSipHandler);
        }
    }
}
