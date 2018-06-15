package org.owen.hermes.server.udp;

import org.owen.hermes.bootstrap.NettySipHandler;
import org.owen.hermes.stub.SipServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.ipc.netty.udp.UdpServer;

/**
 * Created by owen_q on 2018. 6. 15..
 */
public class HermesUdpSipServer extends SipServer{
    private Logger logger = LoggerFactory.getLogger(HermesUdpSipServer.class);

    private String udpListenHost = "";
    private int udpListenPort = 0;

    private UdpServer reactorUdpServer = null;
    private NettySipHandler serverHandler = null;

    @Override
    public void run(boolean isSync) throws Exception {

        // TODO: Implement UDP Server

//        final NettyContext server = UdpServer.create()
//                .newHandler((in, out) -> {
//                    in.receive()
//                            .asByteArray()
//                            .log()
//                            .subscribe(bytes -> {
//                                if (bytes.length == 1024) {
//                                }
//                            });
//                    return Flux.never();
//
//                })
//                .doOnSuccess(v -> {
//                    try {
//                        DatagramChannel udp =
//                                DatagramChannel.open();
//                        udp.configureBlocking(true);
//                        udp.connect(new InetSocketAddress(
//                                InetAddress.getLocalHost(),
//                                port));
//
//                        byte[] data = new byte[1024];
//                        new Random().nextBytes(data);
//                        for (int i = 0; i < 4; i++) {
//                            udp.write(ByteBuffer.wrap(data));
//                        }
//
//                        udp.close();
//                    }
//                    catch (IOException e) {
//                        log.error("", e);
//                    }
//                })
//                .block(Duration.ofSeconds(30));

//
//        UdpServer.create(opts -> opts
//            .host(udpListenHost)
//            .port(udpListenPort)
//                .afterChannelInit()
//        );

    }



}
