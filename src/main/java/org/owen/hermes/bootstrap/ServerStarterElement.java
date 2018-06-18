package org.owen.hermes.bootstrap;

import io.netty.handler.ssl.SslContext;
import org.owen.hermes.bootstrap.handler.HermesAbstractSipHandler;

import java.util.List;

/**
 * Created by owen_q on 2018. 6. 16..
 */
public class ServerStarterElement {
    public String serverListenHost = "";
    public int serverListenPort = 0;
    public SslContext sslContext = null;

    public HermesAbstractSipHandler hermesAbstractSipHandler = null;

    public List<SipMessageHandler> sipMessageHandlerList;
    public SipMessageConsumer sipMessageConsumer = null;

    public ServerStarterElement(String serverListenHost, int serverListenPort, SslContext sslContext, HermesAbstractSipHandler hermesAbstractSipHandler) {
        this.serverListenHost = serverListenHost;
        this.serverListenPort = serverListenPort;
        this.sslContext = sslContext;
        this.hermesAbstractSipHandler = hermesAbstractSipHandler;
    }
}
