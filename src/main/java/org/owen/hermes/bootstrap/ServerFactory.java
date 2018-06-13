package org.owen.hermes.bootstrap;

import org.owen.hermes.model.Transport;
import org.owen.hermes.server.tcp.TcpSipServer;
import org.owen.hermes.stub.SipServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

/**
 * Created by dongqlee on 2018. 6. 13..
 */
public class ServerFactory {
    private Logger logger = LoggerFactory.getLogger(ServerFactory.class);

    private String serverListenHost="";
    private int serverListenPort=0;
    private Transport serverTransport=Transport.NONE;
    private boolean isSSL=false;
    private SipMessageHandler sipMessageHandler =null;


    public ServerFactory host(String serverListenHost){
        this.serverListenHost=serverListenHost;
        return this;
    }

    public ServerFactory port(int serverListenPort){
        this.serverListenPort=serverListenPort;
        return this;
    }

    public ServerFactory transport(Transport serverTransport){
        this.serverTransport=serverTransport;
        return this;
    }

    public ServerFactory ssl(boolean isSSL){
        this.isSSL=isSSL;
        return this;
    }

    public ServerFactory inBoundHandler(SipMessageHandler sipMessageHandler){
        this.sipMessageHandler=sipMessageHandler;
        return this;
    }

    /**
     *
     * Build {@link SipServer} using required parameters
     */
    public SipServer build(){
        SipServer sipServer=null;

        if(Transport.TCP.equals(this.serverTransport)){
            sipServer=TcpSipServer.create(this.serverListenHost, this.serverListenPort, (in, out)->{
                logger.info("In lambda server handler");
                return Flux.never();
            });
        }
        else if(Transport.UDP.equals(this.serverTransport)){
            // TODO:
//            sipServer=new UdpSipServer(sipMessageHandler);
        }
        else if(Transport.TLS.equals(this.serverTransport)){
            // TODO:
//            sipServer = new TcpSipServer(sipMessageHandler, true);
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
