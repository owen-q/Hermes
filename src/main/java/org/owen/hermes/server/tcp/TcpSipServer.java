package org.owen.hermes.server.tcp;

import org.owen.hermes.stub.SipServer;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.NettyContext;
import reactor.ipc.netty.NettyInbound;
import reactor.ipc.netty.NettyOutbound;
import reactor.ipc.netty.NettyPipeline;
import reactor.ipc.netty.tcp.TcpServer;

import java.util.function.BiFunction;

/**
 * Created by dongqlee on 2018. 6. 13..
 */
public class TcpSipServer extends SipServer{
    private Logger logger = LoggerFactory.getLogger(TcpSipServer.class);

    private TcpServer reactorTcpServer=null;
    private Mono<? extends NettyContext> connected=null;
    private Disposable connectedResult=null;

    private TcpSipServer(TcpServer reactorTcpServer, Mono<? extends NettyContext> connected) {
        this.reactorTcpServer=reactorTcpServer;
        this.connected=connected;
    }

    public static TcpSipServer create(String serverHost, int serverPort,
                         BiFunction<? super NettyInbound, ? super NettyOutbound, ? extends Publisher<Void>> serverHandler ){

        TcpServer reactorTcpServer = TcpServer.create(opts -> opts
                .afterChannelInit(c -> c.pipeline()
                        .addBefore(
                                NettyPipeline.ReactiveBridge,
                                "codec",
                                new TcpStreamDecoder())
//                        .addBefore(
//                                NettyPipeline.ReactiveBridge,
//                                "converter",
//                                new SipConverter(Transport.TCP)
//                        )
                )
                .host(serverHost)
                .port(serverPort));

        // Make server runnable
        Mono<? extends NettyContext> runnable = reactorTcpServer.newHandler(serverHandler);

        return new TcpSipServer(reactorTcpServer, runnable);
    }

    public void close(){
        if(logger.isDebugEnabled())
            logger.debug("Stop server...");

        this.connectedResult.dispose();
    }

    @Override
    public void run(boolean isSync) throws Exception {
        if(isSync){
            if(logger.isDebugEnabled())
                logger.debug("Start server as sync");

            connectedResult=this.connected.block();
        }
        else {
            if(logger.isDebugEnabled())
                logger.debug("Start server as async");

            connectedResult=this.connected.subscribe();
        }

    }
}
