package proxy;

import org.owen.hermes.bootstrap.SipMessageConsumer;
import org.owen.hermes.bootstrap.SipMessageHandler;
import org.owen.hermes.bootstrap.server.ServerFactory;
import org.owen.hermes.model.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by owen_q on 2018. 6. 16..
 */
public class Proxy {
    private Logger logger = LoggerFactory.getLogger(Proxy.class);

    public static void main(String[] args) {
        SipMessageHandler<String, String> testSipMessageHandler = (msg) -> {
            return msg;
        };

        SipMessageConsumer<String> testSendHandler = (msg) -> {
            System.out.println("Consume message: \n" + msg);
        };

        ServerFactory serverFactory = new ServerFactory();
        serverFactory
                .host("10.0.8.2")
                .port(10000)
                .transport(Transport.TCP)
                .sipMessageHandler(testSipMessageHandler)
                .sipMessageConsumer(testSendHandler);
    }

}
