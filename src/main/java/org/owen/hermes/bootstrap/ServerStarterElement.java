package org.owen.hermes.bootstrap;

import io.netty.handler.ssl.SslContext;

import java.util.List;

/**
 * Created by owen_q on 2018. 6. 16..
 */
public class ServerStarterElement {
    public String serverListenHost = "";
    public int serverListenPort = 0;
    public SslContext sslContext = null;

    public NettySipHandler nettySipHandler = null;

    public List<SipMessageHandler> sipMessageHandlerList;
    public SipMessageConsumer sipMessageConsumer = null;


    public ServerStarterElement(String serverListenHost, int serverListenPort, SslContext sslContext, NettySipHandler nettySipHandler) {
        this.serverListenHost = serverListenHost;
        this.serverListenPort = serverListenPort;
        this.sslContext = sslContext;
        this.nettySipHandler = nettySipHandler;
    }
}
