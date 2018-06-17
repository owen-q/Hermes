package org.owen.hermes.server.websocket;

import org.owen.hermes.bootstrap.ChannelHandler;
import org.owen.hermes.bootstrap.NettySipHandler;
import org.owen.hermes.bootstrap.ServerStarterElement;
import org.owen.hermes.stub.SipServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.ipc.netty.http.server.HttpServer;
import reactor.ipc.netty.tcp.BlockingNettyContext;

/**
 * Created by owen_q on 2018. 6. 17..
 */
public class HermesWebsocketSipServer extends SipServer{
    private Logger logger = LoggerFactory.getLogger(HermesWebsocketSipServer.class);
    private HttpServer reactorNettyHttpServer = null;
    private NettySipHandler nettySipHandler = null;

    private HermesWebsocketSipServer(HttpServer reactorNettyHttpServer, NettySipHandler nettySipHandler) {
        this.reactorNettyHttpServer = reactorNettyHttpServer;
        this.nettySipHandler = nettySipHandler;
    }

    public static HermesWebsocketSipServer create(ServerStarterElement serverStarterElement){
        HttpServer httpServer;

        httpServer = HttpServer.create(
                opts -> opts
                        .afterChannelInit(channel -> {
                            channel.pipeline().addFirst("hi", new ChannelHandler());
                        })
                        .host(serverStarterElement.serverListenHost)
                        .port(serverStarterElement.serverListenPort)
                        .sslContext(serverStarterElement.sslContext)
        );
        /*
                .newHandler((in, out) -> out.sendWebsocket((i, o) -> o.sendString(
                        Mono.just("test"))));
                        */

        return new HermesWebsocketSipServer(httpServer, serverStarterElement.nettySipHandler);
    }

    // TODO: Refactoring duplicated logic for start server
    @Override
    public void run(boolean isSync) throws Exception {
        if(isSync){
            if(logger.isDebugEnabled())
                logger.debug("Start server as sync");

            // Make blocking server
            BlockingNettyContext blockingNettyContext = this.reactorNettyHttpServer.start(this.nettySipHandler);

            blockingNettyContext.installShutdownHook();
            blockingNettyContext.getContext().onClose().block();
        }
        else {
            if(logger.isDebugEnabled())
                logger.debug("Start server as async");

            BlockingNettyContext blockingNettyContext = this.reactorNettyHttpServer.start(this.nettySipHandler);

        }
    }
}
