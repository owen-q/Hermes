package org.owen.hermes.bootstrap.channel;

import java.net.InetSocketAddress;

import lombok.extern.slf4j.Slf4j;

import org.owen.hermes.core.ConnectionManager;
import org.owen.hermes.model.RemoteAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;

/**
 * Created by owen_q on 2018. 6. 16..
 */
@Slf4j
public class HermesChannelInboundHandler implements ChannelInboundHandler {
    private ConnectionManager connectionManager = ConnectionManager.getInstance();

    private InetSocketAddress inetSocketAddress = null;
    private String remoteHost = "";
    private int remotePort = 0;
    private String transport = "";

    private RemoteAddress getRemoteAddress(ChannelHandlerContext ctx){
        RemoteAddress remoteAddress = new RemoteAddress();
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();

        remoteAddress.host = inetSocketAddress.getHostString();
        remoteAddress.port = inetSocketAddress.getPort();
        remoteAddress.transport = ctx.name();

        return remoteAddress;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        connectionManager.addConnection(ctx);
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        connectionManager.deleteConnection(ctx);
        inetSocketAddress = null;
        ctx.fireChannelUnregistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active");

        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel inactive");
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
        System.out.println("channel add");
//        ctx.channel().handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel removed");
//        ctx.channel().handlerRemoved(ctx);
    }
}