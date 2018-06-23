package org.owen.hermes.bootstrap.server

import org.owen.hermes.bootstrap.SipConsumer
import org.owen.hermes.bootstrap.SipHandler
import org.owen.hermes.model.Transport
import org.owen.hermes.sip.wrapper.message.DefaultSipMessage
import org.owen.hermes.stub.SipServer
import spock.lang.Specification
/**
 * Created by owen_q on 2018. 6. 23..
 */
class ServerFactoryTest extends Specification {

    String givenServerListenHost = "192.168.0.3"
    int givenServerListenPort = 10000
    Transport givenServerTransport = Transport.TCP

    def "create SipServer without MessageHandler & MessageConsumer"() {
        given:

        when:
        ServerFactory serverFactory = new ServerFactory()
        SipServer sipServer = serverFactory
                .host(givenServerListenHost)
                .port(givenServerListenPort)
                .transport(givenServerTransport)
        .build()

        sipServer.run(true)

        then:
        thrown(IllegalArgumentException)
    }


    def "create SipServer with MessageHandler & MessageConsumer"() {
        given:

        SipHandler givenSipMessageHandler = new SipHandler() {
            @Override
            DefaultSipMessage apply(DefaultSipMessage defaultSipMessage) {
                return null
            }
        }

        SipConsumer givenSipMessageConsumer = new SipConsumer() {
            @Override
            void send(DefaultSipMessage defaultSipMessage) {
                println "in consumer.send()"
            }

            @Override
            void accept(DefaultSipMessage defaultSipMessage) {
                println defaultSipMessage

                send(defaultSipMessage)
            }
        }

        when:
        ServerFactory serverFactory = new ServerFactory()
        SipServer sipServer = serverFactory
                .host(givenServerListenHost)
                .port(givenServerListenPort)
                .transport(givenServerTransport)
                .sipMessageHandler(givenSipMessageHandler)
                .sipMessageConsumer(givenSipMessageConsumer)
                .build()

        sipServer.run(true)

        then:
        notThrown(IllegalArgumentException)
    }

    /*
    def "create SipServer"() {
        given:

        SipHandler<String, String> testSipMessageHandler = new SipHandler<String, String>() {
            @Override
            String apply(String s) {
                return null
            }
        }

        expect:
        ServerFactory serverFactory = new ServerFactory()
        SipServer sipServer = serverFactory
                .host(givenServerListenHost)
                .port(givenServerListenPort)
                .transport(givenServerTransport)
                .sipMessageHandler(givenSipMessageHandler)
                .build()

        sipServer.run(true)

        where:

        givenServerListenHost | givenServerListenPort | givenServerTransport | givenSipMessageHandler
        "192.168.0.3"         | 10000 | Transport.TCP |  null
    }
    */
}
