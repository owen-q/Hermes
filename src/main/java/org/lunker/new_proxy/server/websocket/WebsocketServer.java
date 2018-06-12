package org.lunker.new_proxy.server.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.lunker.new_proxy.config.Configuration;
import org.lunker.new_proxy.model.Constants;
import org.lunker.new_proxy.model.Transport;
import org.lunker.new_proxy.sip.processor.ServerProcessor;
import org.lunker.new_proxy.stub.AbstractServer;
import org.lunker.new_proxy.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 31..
 */
public class WebsocketServer extends AbstractServer{
    private Logger logger= LoggerFactory.getLogger(WebsocketServer.class);
    private EventLoopGroup bossGroup=null;
    private EventLoopGroup workerGroup=null;
    private SipMessageHandler sipMessageHandler;
    private SslContext sslContext=null;
    private Transport transport = null;

    public WebsocketServer(SipMessageHandler sipMessageHandler, boolean ssl) {
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
