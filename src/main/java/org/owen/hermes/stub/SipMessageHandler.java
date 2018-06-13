package org.owen.hermes.stub;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.owen.hermes.model.ServerInfo;
import org.owen.hermes.sip.wrapper.message.DefaultSipMessage;

import java.util.Optional;

/**
 * Created by dongqlee on 2018. 4. 25..
 */
public abstract class SipMessageHandler extends ChannelInboundHandlerAdapter {
    private String transport="";
    private ServerInfo serverInfo=null;

//    public SipMessageHandler() {}

    private SipMessageHandler(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public abstract void handle(Optional<DefaultSipMessage> maybeDefaultSipMessage);

    public void setTransport(String transport){
        this.transport=transport;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        handle((Optional<DefaultSipMessage>) msg);
    }
}
