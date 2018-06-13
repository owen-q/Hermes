package org.owen.hermes.server.tcp;

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
import org.owen.hermes.stub.SipServer;
import org.owen.hermes.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 15..
 */
public class TcpSipServer extends SipServer {
    private Logger logger= LoggerFactory.getLogger(TcpSipServer.class);

    //TODO: refactoring
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Transport transport=null;
    private Configuration configuration=Configuration.getInstance();
    private SipMessageHandler sipMessageHandler = null;
    private SslContext sslContext = null;

    public TcpSipServer(SipMessageHandler sipMessageHandler, boolean ssl) {
        // Set Netty channel initializer
        this.sipMessageHandler = sipMessageHandler;

        if (ssl) {
            this.transport = Transport.TLS;
            this.transportConfigMap = Configuration.getInstance().getConfigMap(this.transport);

            try {
                sslContext = SslContextBuilder
                        .forServer(
                        new File((String) transportConfigMap.get(Constants.Options.TLS.SSL_CERT)),
                        new File((String) transportConfigMap.get(Constants.Options.TLS.SSL_KEY)))
                        .build();
            } catch (SSLException e) {
                e.printStackTrace();
            }

            // create channel initializer
            this.channelInitializer = new TcpChannelInitializer(this.sipMessageHandler, sslContext);
        } else {
            this.transport = Transport.TCP;
            this.transportConfigMap = Configuration.getInstance().getConfigMap(this.transport);
            // create channel initializer
            this.channelInitializer = new TcpChannelInitializer(this.sipMessageHandler);
        }
    }

    /**
     * Run TcpSipServer
     * @return
     * @throws Exception
     */
    @Override
    public ChannelFuture run() throws Exception {
        configuration.getConfigMap(transport);
        ServerBootstrap b = new ServerBootstrap();

        Map<String, Object> tcpOptions=(Map<String, Object>)transportConfigMap.get("options");

        // TODO: set ChannelOption using transport properties
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(this.channelInitializer)
                .option(ChannelOption.SO_BACKLOG, (int) tcpOptions.get("so_backlog"))

                .childOption(ChannelOption.SO_LINGER, (int) tcpOptions.get("so_linger"))
                .childOption(ChannelOption.TCP_NODELAY, (boolean) tcpOptions.get("tcp_nodelay"))
                .childOption(ChannelOption.SO_REUSEADDR, (boolean) tcpOptions.get("so_reuseaddr"))

                .childOption(ChannelOption.SO_RCVBUF, (int) tcpOptions.get("so_rcvbuf"))
                .childOption(ChannelOption.SO_SNDBUF, (int) tcpOptions.get("so_sndbuf"));

        // Bind and addHandler to accept incoming connections.
        ChannelFuture channelFuture=b.bind((int) transportConfigMap.get("port")); // (7)

        if(logger.isInfoEnabled())
            logger.info("Run TCP Server Listening on {}", transportConfigMap.get("port"));

        return channelFuture;
    }// end run

    public void shutdown(){
        if(logger.isDebugEnabled())
            logger.debug("Shut down TcpSipServer gracefully...");

        if(workerGroup!=null)
            workerGroup.shutdownGracefully();
        if(bossGroup!=null)
            bossGroup.shutdownGracefully();
    }
}

