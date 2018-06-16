package org.owen.hermes.bootstrap.server;

import org.owen.hermes.bootstrap.NettySipHandler;
import org.owen.hermes.bootstrap.SipMessageHandler;
import org.owen.hermes.model.Transport;
import org.owen.hermes.server.tcp.HermesTcpSipServer;
import org.owen.hermes.server.udp.HermesUdpSipServer;
import org.owen.hermes.stub.SipServer;
import org.owen.hermes.util.CheckUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by owen_q on 2018. 6. 13..
 */
public class ServerFactory {
    private Logger logger = LoggerFactory.getLogger(ServerFactory.class);

    private String serverListenHost = "";
    private int serverListenPort = 0;
    private Transport serverTransport = Transport.NONE;
    private boolean isSSL = false;
    private List<SipMessageHandler> sipMessageHandlerList = null;

    public ServerFactory() {
        this.sipMessageHandlerList = new ArrayList<>();
    }

    public ServerFactory host(String serverListenHost){
        this.serverListenHost = serverListenHost;
        return this;
    }

    public ServerFactory port(int serverListenPort){
        this.serverListenPort = serverListenPort;
        return this;
    }

    public ServerFactory transport(Transport serverTransport){
        this.serverTransport = serverTransport;
        return this;
    }

    public ServerFactory ssl(boolean isSSL){
        this.isSSL = isSSL;
        return this;
    }

    public ServerFactory sipMessageHandler(SipMessageHandler sipMessageHandler){
        if(!this.sipMessageHandlerList.contains(sipMessageHandler))
            this.sipMessageHandlerList.add(sipMessageHandler);
        else
            throw new IllegalArgumentException("Duplicated sipMessageHandler");

        return this;
    }

    /**
     *
     * Build {@link SipServer} using required parameters
     */
    public SipServer build(){
        CheckUtil.checkEmptyString(this.serverListenHost, "ServerListenHost");
        CheckUtil.checkNotZero(this.serverListenPort, "ServerListenPort");
        CheckUtil.checkNotEqual(this.serverTransport, Transport.NONE, "ServerTransport");
        CheckUtil.checkCollectionNotEmpty(this.sipMessageHandlerList, "SipMessageHandlerList");
        // TODO: handler chaining type check

        SipServer sipServer = null;
        NettySipHandler nettySipHandler = null;

        nettySipHandler = NettySipHandler.create(this.sipMessageHandlerList);

        if(Transport.TCP.equals(this.serverTransport)){
            sipServer = HermesTcpSipServer.create(this.serverListenHost, this.serverListenPort, nettySipHandler);
        }
        else if(Transport.UDP.equals(this.serverTransport)){
            // TODO:
            sipServer = HermesUdpSipServer.create(this.serverListenHost, this.serverListenPort, nettySipHandler);
        }
        else if(Transport.TLS.equals(this.serverTransport)){
            // TODO:
//            sipServer = new HermesTcpSipServer(sipMessageHandler, true);
        }
        else if(Transport.WS.equals(this.serverTransport)){
            // TODO:
//            sipServer=new WebsocketSipServer(sipMessageHandler, false);
        }
        else if(Transport.WSS.equals(this.serverTransport)){
            // TODO:
//            sipServer=new WebsocketSipServer(sipMessageHandler, true);
        }
        else{

        }

        return sipServer;
    }
}
