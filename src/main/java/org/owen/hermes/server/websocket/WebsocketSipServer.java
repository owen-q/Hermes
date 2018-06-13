package org.owen.hermes.server.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.owen.hermes.config.Configuration;
import org.owen.hermes.model.Constants;
import org.owen.hermes.model.Transport;
import org.owen.hermes.stub.AbstractServer;
import org.owen.hermes.stub.SipServer;
import org.owen.hermes.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by dongqlee on 2018. 3. 31..
 */
public class WebsocketSipServer extends SipServer {
    private Logger logger= LoggerFactory.getLogger(WebsocketSipServer.class);
    private EventLoopGroup bossGroup=null;
    private EventLoopGroup workerGroup=null;
    private SipMessageHandler sipMessageHandler;
    private SslContext sslContext=null;
    private Transport transport = null;

    public WebsocketSipServer(SipMessageHandler sipMessageHandler, boolean ssl) {
        this.sipMessageHandler = sipMessageHandler;
        // Set Netty channel initializer
        if(ssl){
            try{
                this.transport = Transport.WSS;
                // Set transport configs
                this.transportConfigMap = Configuration.getInstance().getConfigMap(this.transport);
                sslContext = SslContextBuilder
                        .forServer(
                                new File((String) transportConfigMap.get(Constants.Options.WSS.SSL_CERT)),
                                new File((String) transportConfigMap.get(Constants.Options.WSS.SSL_KEY)))
                        .build();
                this.channelInitializer = new WebsocketChannelInitializer(this.sipMessageHandler, sslContext);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        } else {
            this.transport = Transport.WS;
            this.transportConfigMap = Configuration.getInstance().getConfigMap(this.transport);
            this.channelInitializer = new WebsocketChannelInitializer(this.sipMessageHandler);
        }

        this.channelInitializer=new WebsocketChannelInitializer(this.sipMessageHandler, sslContext);


    }

    @Override
    public ChannelFuture run() throws InterruptedException {
        // Configure the server.
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();

        b.option(ChannelOption.SO_BACKLOG, 1024);
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(this.channelInitializer);

        ChannelFuture channelFuture=b.bind((int) transportConfigMap.get("port")).sync(); // (7)

        logger.info("Run Websocket Server Listening on " + (int) transportConfigMap.get("port"));

        return channelFuture;
    }

    // TODO:
    public void shutdown(){
        logger.info("Shut down Websocket Server gracefully...");
        if(bossGroup!=null)
            bossGroup.shutdownGracefully();
        if (workerGroup!=null)
            workerGroup.shutdownGracefully();
    }
}
