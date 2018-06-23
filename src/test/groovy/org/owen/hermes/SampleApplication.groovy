package org.owen.hermes

import gov.nist.javax.sip.message.SIPResponse
import org.owen.hermes.bootstrap.SipConsumer
import org.owen.hermes.bootstrap.SipHandler
import org.owen.hermes.bootstrap.server.ServerFactory
import org.owen.hermes.model.Transport
import org.owen.hermes.sip.wrapper.message.DefaultSipMessage
import org.owen.hermes.sip.wrapper.message.DefaultSipRequest
import org.owen.hermes.sip.wrapper.message.DefaultSipResponse
import org.owen.hermes.stub.SipServer
import spock.lang.Specification
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
                println "Send SipMessage: \n " + defaultSipMessage
            }

            @Override
            void accept(DefaultSipMessage defaultSipMessage) {
                println "In sipConsumer accept()"
                send(defaultSipMessage)
            }
        }

        String givenServerListenHost = "10.0.8.2"
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

    /*
    def "run sipClient"() {
        given:

        when:

        then:
    }
    */
}
