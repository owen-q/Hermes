package org.owen.hermes.client.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import org.owen.hermes.config.Configuration;
import org.owen.hermes.model.Transport;
import org.owen.hermes.server.udp.UdpServerHandler;
import org.owen.hermes.sip.processor.SipPreProcessor;
import org.owen.hermes.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Map;

public class UdpClient {
    private Logger logger = LoggerFactory.getLogger(UdpClient.class);

    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private final Transport transport;
    private Configuration configuration;
    private Map<String, Object> transportConfigMap;
    private SipMessageHandler sipMessageHandler;

    private Channel channel;

    public UdpClient(Class SipMessageHandlerImpl) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        transport = Transport.UDP;
        configuration = Configuration.getInstance();
        transportConfigMap = configuration.getConfigMap(Transport.UDP);
        this.sipMessageHandler = (SipMessageHandler) SipMessageHandlerImpl.getConstructor().newInstance();
    }

    public ChannelFuture start() throws InterruptedException {
        workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioDatagramChannel.class)
                .handler(new ChannelInitializer<DatagramChannel>() {
                    @Override
                    protected void initChannel(DatagramChannel ch) throws Exception {
                        // TODO: add decode handler or somthing like...
                        ch.pipeline().addLast(new SipPreProcessor(transport));
                        ch.pipeline().addLast(sipMessageHandler);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        logger.error(cause.getMessage());
                    }
                });
        ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(0));
        channelFuture.syncUninterruptibly();

        channel = channelFuture.channel();

        return channelFuture;
    }

    public ChannelFuture write(String msg, String host, int port) throws InterruptedException {
        return channel.writeAndFlush(
                new DatagramPacket(
                        Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8),
                        new InetSocketAddress(host, port))
        ).sync();
    }
    public ChannelFuture write(DatagramPacket packet) throws InterruptedException {
        return channel.writeAndFlush(packet).sync();
    }

    public void stop() {
        if (channel != null)
            channel.close();
        workerGroup.shutdownGracefully();
    }

//    public ChannelFuture connect(String host, int port) throws Exception {
//        Bootstrap bootstrap = new Bootstrap();
//        Map<String, Object> udpOptions = (Map<String, Object>)transportConfigMap.get("options");
//
//        bootstrap.group(workerGroup)
//                .channel(NioDatagramChannel.class)
//                .handler(new ChannelInitializer<DatagramChannel>() {
//                    @Override
//                    protected void initChannel(DatagramChannel ch) throws Exception {
//                        ch.pipeline().addLast(new SipPreProcessor(transport));
//                        ch.pipeline().addLast(sipMessageHandler);
//                    }
//
//                    @Override
//                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//                        logger.error(cause.getMessage());
//                    }
//                });
//
//        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
//        return channelFuture;
//    }
}
