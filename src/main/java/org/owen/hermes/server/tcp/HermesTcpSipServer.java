package org.owen.hermes.server.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOption;
import org.owen.hermes.bootstrap.NettySipHandler;
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
    private NettySipHandler serverHandler = null;

    //BiFunction<NettyInbound, NettyOutbound, ? extends Publisher<Void>>
    private HermesTcpSipServer(TcpServer reactorTcpServer, NettySipHandler serverHandler) {
        this.reactorTcpServer = reactorTcpServer;
        this.serverHandler = serverHandler;
    }

    public static HermesTcpSipServer create(String serverHost, int serverPort,
                                            NettySipHandler serverHandler ){

        TcpServer reactorTcpServer =
                TcpServer.create(opts -> opts
                        .afterChannelInit((channel -> {
                            channel.pipeline().addFirst("hi", new TestChannelInit());
                        }))
                        .host(serverHost)
                        .port(serverPort)
                        .option(ChannelOption.AUTO_READ,true) // turn on for handling SIP
                        .option(ChannelOption.SO_KEEPALIVE, false)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .option(ChannelOption.SO_LINGER, 0 )
                        .option(ChannelOption.SO_REUSEADDR, true)

                );

        return new HermesTcpSipServer(reactorTcpServer, serverHandler);
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


class TestChannelInit implements ChannelInboundHandler{

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelUnregistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active");
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channel read");
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelWritabilityChanged();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handler add ");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handler removed ");
    }
}