package proxy;

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
        ServerFactory serverFactory = new ServerFactory();
        serverFactory
                .host("10.0.1.202")
                .port(10000)
                .transport(Transport.TCP);
//                .sipMessageHandler(registerHandler::handleRegister)
//                .sipConsumer(testSendHandler);



    }

}
