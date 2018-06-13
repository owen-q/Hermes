package org.owen.hermes.server.tcp;

import io.netty.channel.ChannelOption;
import org.owen.hermes.model.Transport;
import org.owen.hermes.sip.processor.SipConverter;
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
import reactor.ipc.netty.tcp.BlockingNettyContext;
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

    private BiFunction<NettyInbound, NettyOutbound, ? extends Publisher<Void>> serverHandler;


    private TcpSipServer(TcpServer reactorTcpServer, BiFunction<NettyInbound, NettyOutbound, ? extends Publisher<Void>> serverHandler) {
        this.reactorTcpServer = reactorTcpServer;
        this.serverHandler=serverHandler;
    }

    private TcpSipServer(TcpServer reactorTcpServer, Mono<? extends NettyContext> connected) {
        this.reactorTcpServer=reactorTcpServer;
        this.connected=connected;
    }

    public static TcpSipServer create(String serverHost, int serverPort,
                         BiFunction<NettyInbound, NettyOutbound, ? extends Publisher<Void>> serverHandler ){

        TcpServer simpleServer =
                TcpServer.create(opts -> opts
                        .afterChannelInit(
                                c -> c.pipeline()
                                        .addBefore(
                                                NettyPipeline.ReactiveBridge,
                                            "codec",
                                            new TcpStreamDecoder())
                                        .addBefore(
                                                NettyPipeline.ReactiveBridge,
                                            "converter",
                                            new SipConverter(Transport.TCP)))
                        .host(serverHost)
                        .port(serverPort)
                        .option(ChannelOption.AUTO_READ,true) // turn on for handling SIP
                        .option(ChannelOption.SO_KEEPALIVE, false)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .option(ChannelOption.SO_LINGER, 0 )
                        .option(ChannelOption.SO_REUSEADDR, true)
                );

        return new TcpSipServer(simpleServer, serverHandler);
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

            // Make blocking server
            BlockingNettyContext blockingNettyContext=
                    this.reactorTcpServer.start((in, out)->{
                        System.out.println("fuck!");
                        in.receive().asString().subscribe((msg)->{
                            System.out.println("hihihi");
                            System.out.println(msg);
                        });

                        out.options(NettyPipeline.SendOptions::flushOnEach);

                        return out;
            });

            blockingNettyContext.installShutdownHook();
            blockingNettyContext.getContext().onClose().block();
        }
        else {
            if(logger.isDebugEnabled())
                logger.debug("Start server as async");

            connectedResult=this.connected.subscribe();
        }

    }
}
