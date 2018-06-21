package org.owen.hermes.bootstrap.server;

import io.netty.handler.ssl.SslContext;
import org.owen.hermes.bootstrap.ServerStarterElement;
import org.owen.hermes.bootstrap.SipMessageConsumer;
import org.owen.hermes.bootstrap.SipMessageHandler;
import org.owen.hermes.bootstrap.handler.HermesAbstractSipHandler;
import org.owen.hermes.model.Transport;
import org.owen.hermes.server.tcp.HermesTcpSipServer;
import org.owen.hermes.server.udp.HermesUdpSipServer;
import org.owen.hermes.server.websocket.HermesWebsocketSipServer;
import org.owen.hermes.stub.SipServer;
import org.owen.hermes.util.CheckUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by owen_q on 2018. 6. 13..
 */
public class ServerFactory {
    private Logger logger = LoggerFactory.getLogger(ServerFactory.class);

    private String serverListenHost = "";
    private int serverListenPort = 0;
    private Transport serverTransport = Transport.NONE;

    private SslContext sslContext = null;

    private List<SipMessageHandler> sipMessageHandlerList = null;
    private SipMessageConsumer sipMessageConsumer = null;

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

    public ServerFactory sslContext(SslContext sslContext){
        if(sslContext != null && this.serverTransport == Transport.UDP)
            throw new IllegalArgumentException("Cannot assign SSL Options for udp server");

        this.sslContext = sslContext;
        return this;
    }

    public ServerFactory options(){
        return this;
    }


    public ServerFactory sipMessageHandler(Function<Object, Object> sipMessageHandler){
        logger.debug("Register Function<Object, Object> sipHandlers");
        return this;
    }


    public ServerFactory sipMessageHandler(SipMessageHandler sipMessageHandler){
        /*
        if(!(sipMessageHandler instanceof Function))
            throw new IllegalArgumentException("SipMessageHandler must implements Function interface");
        */

        if(!this.sipMessageHandlerList.contains(sipMessageHandler))
            this.sipMessageHandlerList.add(sipMessageHandler);
        else
            throw new IllegalArgumentException("Duplicated sipMessageHandler");

        return this;
    }

    public ServerFactory sipMessageConsumer(SipMessageConsumer sipMessageConsumer) {
        this.sipMessageConsumer = sipMessageConsumer;
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
        CheckUtil.checkNotNull(this.sipMessageConsumer, SipMessageConsumer.class.getName());
        // TODO: channel chaining type check

        HermesAbstractSipHandler hermesAbstractSipHandler = null;
        ServerStarterElement serverStarterElement = null;
        SipServer sipServer = null;


        hermesAbstractSipHandler = HermesAbstractSipHandler.create(this.serverTransport, this.sipMessageHandlerList, this.sipMessageConsumer);

        serverStarterElement = new ServerStarterElement(this.serverListenHost, this.serverListenPort, this.sslContext, hermesAbstractSipHandler);

        if(Transport.TCP.equals(this.serverTransport) || Transport.TLS.equals(this.serverTransport)){
            sipServer = HermesTcpSipServer.create(serverStarterElement);
        }
        else if(Transport.UDP.equals(this.serverTransport)){
            // TODO:
            sipServer = HermesUdpSipServer.create(serverStarterElement);
        }
        else if(Transport.WS.equals(this.serverTransport) || Transport.WSS.equals(this.serverTransport)){
            // TODO:
            sipServer = HermesWebsocketSipServer.create(serverStarterElement);
        }
        else{

        }

        return sipServer;
    }
}
