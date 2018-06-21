package hermes.bootstrap.server;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.owen.hermes.bootstrap.SipMessageConsumer;
import org.owen.hermes.bootstrap.SipMessageHandler;
import org.owen.hermes.bootstrap.server.ServerFactory;
import org.owen.hermes.model.Transport;
import org.owen.hermes.stub.SipServer;

/**
 * Created by owen_q on 2018. 6. 13..
 */
public class TestServerFactory {
    private String host="10.0.1.202";
//    private String host = "10.0.8.2";
//    private String host = "192.168.50.32";

    private int port = 10000;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testCreateTcpServerWithoutHost(){
        expectedException.expect(IllegalArgumentException.class);

        ServerFactory serverFactory = new ServerFactory();
        SipServer sipServer = null;

        sipServer = serverFactory.port(port).transport(Transport.TCP).build();
    }

    @Test
    public void testCreateTcpServerWithoutPort() throws Exception{
        expectedException.expect(IllegalArgumentException.class);

        ServerFactory serverFactory = new ServerFactory();
        SipServer sipServer = null;

        sipServer = serverFactory.host(host).transport(Transport.TCP).build();
    }

    @Test
    public void testCreateTcpServerWithoutTransport(){
        expectedException.expect(IllegalArgumentException.class);

        ServerFactory serverFactory = new ServerFactory();
        SipServer sipServer = null;

        sipServer = serverFactory.host(host).port(port).build();
    }

    @Test
    public void testCreateTcpServerWithDuplicatedSipHandler(){
        expectedException.expect(IllegalArgumentException.class);

        ServerFactory serverFactory=new ServerFactory();
        SipServer sipServer=null;

        SipMessageHandler<String, String> firstHandler=(msg)->{
            return "!";
        };

        sipServer=serverFactory
                .host(host).port(port).transport(Transport.TCP)
                .sipMessageHandler(firstHandler)
                .sipMessageHandler(firstHandler)
                .sipMessageHandler(firstHandler)
                .build();
    }

    public String testHanlder(String testMessage){
        return "in testHandler method\n" + testMessage;
    }

    @Test
    public void testCreateTcpServerWithRegister() throws Exception{
        ServerFactory serverFactory = new ServerFactory();
        SipServer sipServer = null;

        SipMessageHandler<String, String> firstHandler = (msg)->{
//            return msg + "\n First channel!";
            return msg;
        };

        SipMessageHandler<String, String> secondHandler = (msg)->{
//            return msg + "\n Second channel!!!";
            return msg;
        };

        SipMessageConsumer<String> thirdHandler = (msg) -> {
//            System.out.println("SipConsumer :\n" + msg);
        };

        sipServer = serverFactory
                .host(host).port(port).transport(Transport.TCP)
                .sipMessageHandler(firstHandler)
                .sipMessageHandler(secondHandler)
//                .sipMessageHandler(testHanlder)
                .sipMessageConsumer(thirdHandler)
                .build();

        sipServer.runSync();
    }

    @Test
    public void testCreateUdpServer() throws Exception{
        ServerFactory serverFactory = new ServerFactory();
        SipServer sipServer = null;

        SipMessageHandler<String, String> firstHandler = (msg)->{
            return msg + "\n First channel!";
        };

        SipMessageHandler<String, String> secondHandler = (msg)->{
            return msg + "\n Second channel!!!";
        };

        sipServer=serverFactory
                .host(host).port(port).transport(Transport.UDP)
                .sipMessageHandler(firstHandler)
                .sipMessageHandler(secondHandler)
                .build();

        sipServer.runSync();
    }

    @Test
    public void testCreateWebsocketServer() throws Exception{
        ServerFactory serverFactory = new ServerFactory();
        SipServer sipServer = null;

        SipMessageHandler<String, String> firstHandler = (msg)->{
            return msg + "\n First channel!";
        };

        SipMessageHandler<String, String> secondHandler = (msg)->{
            return msg + "\n Second channel!!!";
        };

        SipMessageConsumer<String> consumerHandler = msg -> System.out.println(msg);

        sipServer=serverFactory
                .host(host).port(port).transport(Transport.WS)
                .sipMessageHandler(firstHandler)
                .sipMessageHandler(secondHandler)
                .sipMessageConsumer(consumerHandler)
                .build();

        sipServer.runSync();
    }

}
