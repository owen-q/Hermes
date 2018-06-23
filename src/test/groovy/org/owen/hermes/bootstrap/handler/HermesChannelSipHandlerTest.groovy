package org.owen.hermes.bootstrap.handler

import io.netty.buffer.Unpooled
import org.owen.hermes.bootstrap.SipConsumer
import org.owen.hermes.bootstrap.SipHandler
import org.owen.hermes.sip.wrapper.message.DefaultSipMessage
import reactor.core.publisher.Flux
import reactor.ipc.netty.ByteBufFlux
import reactor.ipc.netty.NettyInbound
import spock.lang.Specification
/**
 * Created by owen_q on 2018. 6. 23..
 */
class HermesChannelSipHandlerTest extends Specification {

    def "test channel chaining"() {
        given:
        String givenWrongSipMessage = "fhi!"
        String givenRegisterMessage = "REGISTER sip:203.240.153.30:5061;transport=tcp SIP/2.0\n" +
                "Max-Forwards: 70\n" +
                "Via: SIP/2.0/TCP 10.0.1.48:5060;branch=z9hG4bK3f34e927a;rport=14324;received=218.51.112.46\n" +
                "Call-ID: ae02d002c7cecdce0151ba8eea11746a@10.0.1.48\n" +
                "From: \"owen\" <sip:owen-q@sapmle.com:5061>;tag=ab4e0ebcd7bcdc3\n" +
                "To: \"owen\" <sip:owen-q@sapmle.com:5061>\n" +
                "Route: <sip:203.240.153.30:5061;lr;transport=tcp>\n" +
                "CSeq: 86545 REGISTER\n" +
                "Contact: <sip:owen@10.0.1.48:5060;maddr=203.240.153.14;transport=tcp>;expires=810\n" +
                "Proxy-Require: com.nortelnetworks.firewall\n" +
                "Expires: 810\n" +
                "Authorization: Digest response=\"a32dfc1a2710424e2d9ab92001dd2c2d\",nc=0001520f,username=\"owen\",realm=\"sample.com\",nonce=\"d9d0941a41e55c0c1cba9dd300059879\",algorithm=MD5,qop=auth,cnonce=\"a439f4b332a18664a8685d13841ba4a1\",uri=\"sip:203.240.153.30:5061;transport=tcp\"\n" +
                "User-Agent: Stonehenge IP255FA 1.30.398\n" +
                "Lb-Ip: 203.240.153.11\n" +
                "Lb-Port: 5072\n" +
                "Client-Ip: 218.51.112.46\n" +
                "Client-Port: 14324\n" +
                "Content-Length: 0"

        NettyInbound givenNettyInbound = Mock(NettyInbound.class)
        List<SipHandler> givenSipHandlers = new ArrayList<>()

        SipConsumer givenSipConsumer = new SipConsumer() {
            @Override
            void send(DefaultSipMessage defaultSipMessage) {

            }

            @Override
            void accept(DefaultSipMessage defaultSipMessage) {
                send(defaultSipMessage)
            }
        }

        HermesChannelSipHandler hermesChannelSipHandler = new HermesChannelSipHandler(givenSipHandlers, givenSipConsumer)

        when:
        givenNettyInbound.receive() >> ByteBufFlux.fromInbound(Flux.just(Unpooled.wrappedBuffer(givenWrongSipMessage.getBytes())))

        hermesChannelSipHandler.chain(givenNettyInbound)

        then:
        notThrown(Exception)
    }

}
