package org.owen.hermes.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.ipc.netty.NettyOutbound;

/**
 * Created by owen_q on 2018. 6. 15..
 */
public class ClientConnection {
    private Logger logger = LoggerFactory.getLogger(ClientConnection.class);

    private NettyOutbound nettyOutbound=null;

    public ClientConnection(NettyOutbound nettyOutboundConnection) {
        this.nettyOutbound = nettyOutboundConnection;
    }

    public NettyOutbound getNettyConnection() {
        return nettyOutbound;
    }

    // TODO:
    public void send(){

    }
}
