package org.owen.hermes.server.udp;

import lombok.extern.slf4j.Slf4j;

import org.owen.hermes.bootstrap.ServerStarterElement;
import org.owen.hermes.bootstrap.channel.HermesChannelInboundHandler;
import org.owen.hermes.bootstrap.handler.HermesAbstractSipHandler;
import org.owen.hermes.model.Transport;
import org.owen.hermes.stub.SipServer;

import reactor.ipc.netty.tcp.BlockingNettyContext;
import reactor.ipc.netty.udp.UdpServer;

/**
 * Created by owen_q on 2018. 6. 15..
 */
@Slf4j
public class HermesUdpSipServer extends SipServer{
    private String udpServerListenHost = "";
    private int udpServerListenPort = 0;

    private UdpServer reactorUdpServer = null;
    private HermesAbstractSipHandler hermesAbstractSipHandler = null;

    public HermesUdpSipServer(UdpServer reactorUdpServer, HermesAbstractSipHandler hermesAbstractSipHandler) {
        this.reactorUdpServer = reactorUdpServer;
        this.hermesAbstractSipHandler = hermesAbstractSipHandler;
    }

    private HermesUdpSipServer(String udpServerListenHost, int udpServerListenPort, HermesAbstractSipHandler hermesAbstractSipHandler) {
        this.udpServerListenHost = udpServerListenHost;
        this.udpServerListenPort = udpServerListenPort;
        this.hermesAbstractSipHandler = hermesAbstractSipHandler;
    }

    public static HermesUdpSipServer create(ServerStarterElement serverStarterElement){
        // TODO: Implement UDP Server

        UdpServer reactorUdpServer = UdpServer.create(opts -> opts
                .host(serverStarterElement.serverListenHost)
                .port(serverStarterElement.serverListenPort)
                .afterChannelInit(channel -> channel.pipeline().addFirst(Transport.UDP.getValue(), new HermesChannelInboundHandler()))
                .sslContext(serverStarterElement.sslContext)
        );

        return new HermesUdpSipServer(reactorUdpServer, serverStarterElement.hermesAbstractSipHandler);
    }

    @Override
    public void run(boolean isSync) throws Exception {

        if(isSync){
            if(log.isDebugEnabled())
                log.debug("Start server as sync");

            // Make blocking server
            BlockingNettyContext blockingNettyContext = this.reactorUdpServer.start(this.hermesAbstractSipHandler);

            blockingNettyContext.installShutdownHook();
            blockingNettyContext.getContext().onClose().block();
        }
        else {
            if(log.isDebugEnabled())
                log.debug("Start server as async");

            BlockingNettyContext blockingNettyContext = this.reactorUdpServer.start(this.hermesAbstractSipHandler);

        }
    }
}
