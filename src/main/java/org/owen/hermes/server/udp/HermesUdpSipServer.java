package org.owen.hermes.server.udp;

import org.owen.hermes.bootstrap.channel.HermesChannelInboundHandler;
import org.owen.hermes.bootstrap.handler.HermesAbstractSipHandler;
import org.owen.hermes.bootstrap.ServerStarterElement;
import org.owen.hermes.model.Transport;
import org.owen.hermes.stub.SipServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.ipc.netty.tcp.BlockingNettyContext;
import reactor.ipc.netty.udp.UdpServer;

/**
 * Created by owen_q on 2018. 6. 15..
 */
public class HermesUdpSipServer extends SipServer{
    private Logger logger = LoggerFactory.getLogger(HermesUdpSipServer.class);

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

        /*
        final NettyContext server = UdpServer.create()
                .newHandler((in, out) -> {
                    in.receive()
                            .asByteArray()
                            .log()
                            .subscribe(bytes -> {
                                if (bytes.length == 1024) {
                                }
                            });
                    return Flux.never();

                })
                .doOnSuccess(v -> {
                    try {
                        DatagramChannel udp =
                                DatagramChannel.open();
                        udp.configureBlocking(true);
                        udp.connect(new InetSocketAddress(
                                InetAddress.getLocalHost(),
                                port));

                        byte[] data = new byte[1024];
                        new Random().nextBytes(data);
                        for (int i = 0; i < 4; i++) {
                            udp.write(ByteBuffer.wrap(data));
                        }

                        udp.close();
                    }
                    catch (IOException e) {
                        log.error("", e);
                    }
                })
                .block(Duration.ofSeconds(30));
        */

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
            if(logger.isDebugEnabled())
                logger.debug("Start server as sync");

            // Make blocking server
            BlockingNettyContext blockingNettyContext = this.reactorUdpServer.start(this.hermesAbstractSipHandler);

            blockingNettyContext.installShutdownHook();
            blockingNettyContext.getContext().onClose().block();
        }
        else {
            if(logger.isDebugEnabled())
                logger.debug("Start server as async");

            BlockingNettyContext blockingNettyContext = this.reactorUdpServer.start(this.hermesAbstractSipHandler);

        }
    }
}
