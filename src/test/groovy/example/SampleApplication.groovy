package example

import gov.nist.javax.sip.address.SipUri
import gov.nist.javax.sip.header.RouteList
import gov.nist.javax.sip.header.Via
import gov.nist.javax.sip.message.SIPMessage
import gov.nist.javax.sip.message.SIPRequest
import gov.nist.javax.sip.message.SIPResponse
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.util.CharsetUtil
import org.owen.hermes.bootstrap.SipConsumer
import org.owen.hermes.bootstrap.SipHandler
import org.owen.hermes.bootstrap.server.ServerFactory
import org.owen.hermes.core.ConnectionManager
import org.owen.hermes.model.Transport
import org.owen.hermes.sip.wrapper.message.DefaultSipMessage
import org.owen.hermes.sip.wrapper.message.DefaultSipRequest
import org.owen.hermes.sip.wrapper.message.DefaultSipResponse
import org.owen.hermes.stub.SipServer
import spock.lang.Specification

import javax.sip.header.RouteHeader
/**
 * Created by owen_q on 2018. 6. 23..
 */
class SampleApplication extends Specification{
    def "run test proxy"() {
        given:

        SipHandler sipHandler = new SipHandler() {
            @Override
            DefaultSipMessage apply(DefaultSipMessage defaultSipMessage) {
                if(defaultSipMessage instanceof DefaultSipRequest){
                    DefaultSipRequest registerRequest = (DefaultSipRequest) defaultSipMessage

                    DefaultSipResponse responseRegisterOK = registerRequest.createResponse(SIPResponse.OK)

                    return responseRegisterOK
                }
            }
        }

        SipConsumer sipConsumer = new SipConsumer() {
            @Override
            void send(DefaultSipMessage defaultSipMessage) {
                ConnectionManager connectionManager = ConnectionManager.getInstance()

                String remoteHost = "";
                int remotePort = 0;
                String remoteTransport = "";
                ChannelHandlerContext targetCtx = null;

                SIPMessage message = defaultSipMessage.getRawSipMessage()

                /**
                 * Request:
                 *
                 * // Client -> proxy (Direct Connection)
                 *  1) - request uri 정보를 읽어와서 client connection을 뒤지고, 해당 socket에 전송한다
                 *
                 * // Client -> LB -> Proxy
                 *  2) - request uri의 정보를 읽어와서 client connection을 못찾으면,
                 *  top via의 정보를 읽어서 해당 노드의 정보가 LB인지 확인한다.
                 *  LB와 일치하면, 해당 LB에게 전송한다.
                 *
                 * Response:
                 *  -> Via를 뒤져서, 해당 connection을 직접 가지고 있으면 전송, 아니면 lb에게 전송
                 *  -> Via를 뒤져서,
                 */
                if(defaultSipMessage.getRawSipMessage() instanceof SIPRequest){
                    RouteList routeList = defaultSipMessage.getRawSipMessage().getRouteHeaders();

                    if(routeList != null && routeList.size() !=0){
                        // Contains Route header
                        // TODO: Find connection using 'Route' Header
                        RouteHeader routeHeader=(RouteHeader) defaultSipMessage.getRouteHeaders().getFirst();
                        SipUri routeUri=(SipUri) routeHeader.getAddress().getURI();

                        remoteHost=routeUri.getHost();
                        remotePort=routeUri.getPort();
                        remoteTransport=routeUri.getTransportParam();
                    }
                    else{
                        // No Route header
                        // Using request-uri to find target connection
                        SIPRequest sipRequest=(SIPRequest) message;
                        SipUri requestUri=(SipUri) sipRequest.getRequestURI();

                        remoteHost=requestUri.getHost();
                        remotePort=requestUri.getPort();
                        remoteTransport=requestUri.getTransportParam();
                    }
                }
                else{
                    // Response

                    SIPResponse sipResponse=(SIPResponse) message;

                    Via topVia = sipResponse.getTopmostVia();
                    remoteHost = topVia.getReceived();
                    remotePort = topVia.getRPort();

                    remoteTransport = topVia.getTransport().toLowerCase();
                }

                targetCtx = connectionManager.getConnection(remoteHost, remotePort, remoteTransport);

                if(targetCtx != null){
                    ChannelFuture cf;
                    if(Transport.TCP.getValue().equals(remoteTransport)){
                        cf = targetCtx.writeAndFlush((Unpooled.copiedBuffer(message.toString(), CharsetUtil.UTF_8)));
                    }
                    else if(Transport.UDP.getValue().equals(remoteTransport)){
                        cf = targetCtx.writeAndFlush(new DatagramPacket(
                                Unpooled.copiedBuffer(message.toString(), CharsetUtil.UTF_8),
                                new InetSocketAddress(remoteHost, remotePort)));
                    }
                    else if(Transport.WSS.getValue().equals(remoteTransport)){
                        cf = targetCtx.writeAndFlush(new TextWebSocketFrame(message.toString()));
                    }
                }
                else {
                }
            }

            @Override
            void accept(DefaultSipMessage defaultSipMessage) {

                // Do Some stuffs here

                // Send Message to target!
                send(defaultSipMessage)
            }
        }

        String givenServerListenHost = "127.0.0.1"
        int givenServerListenPort = 10000
        Transport givenServerTransport = Transport.TCP

        when:
        ServerFactory serverFactory = new ServerFactory()
        SipServer sipServer = serverFactory
                .host(givenServerListenHost)
                .port(givenServerListenPort)
                .transport(givenServerTransport)
                .sipMessageHandler(sipHandler)
                .sipMessageConsumer(sipConsumer).build()

        then:
        sipServer.runAsync()
    }
}
