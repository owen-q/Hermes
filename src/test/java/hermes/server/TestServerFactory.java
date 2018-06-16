package hermes.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.owen.hermes.bootstrap.server.ServerFactory;
import org.owen.hermes.bootstrap.SipMessageHandler;
import org.owen.hermes.model.Transport;
import org.owen.hermes.stub.SipServer;

/**
 * Created by owen_q on 2018. 6. 13..
 */
public class TestServerFactory {
//    private String host="10.0.1.202";
    private String host="10.0.8.2";
    private int port=10000;

    @Rule
    public ExpectedException expectedException=ExpectedException.none();

    @Before
    public void beforeTest(){
    }

    @After
    public void afterTest(){

    }

    @Test
    public void testCreateTcpServerWithoutHost(){
        expectedException.expect(IllegalArgumentException.class);

        ServerFactory serverFactory=new ServerFactory();
        SipServer sipServer=null;

        sipServer=serverFactory.port(port).transport(Transport.TCP).build();
    }


    @Test
    public void testCreateTcpServerWithoutPort() throws Exception{
        expectedException.expect(IllegalArgumentException.class);

        ServerFactory serverFactory=new ServerFactory();
        SipServer sipServer=null;

        sipServer=serverFactory.host(host).transport(Transport.TCP).build();
    }

    @Test
    public void testCreateTcpServerWithoutTransport(){
        expectedException.expect(IllegalArgumentException.class);

        ServerFactory serverFactory=new ServerFactory();
        SipServer sipServer=null;

        sipServer=serverFactory.host(host).port(port).build();
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

    @Test
    public void testTcpCreateServer() throws Exception{
        ServerFactory serverFactory = new ServerFactory();
        SipServer sipServer = null;

        SipMessageHandler<String, String> firstHandler=(msg)->{
            return msg + "\n First handler!";
        };

        SipMessageHandler<String, String> secondHandler=(msg)->{
            return msg + "\n Second handler!!!";
        };

        sipServer=serverFactory
                .host(host).port(port).transport(Transport.TCP)
                .sipMessageHandler(firstHandler)
                .sipMessageHandler(secondHandler)
                .build();

        sipServer.runSync();
    }

    @Test
    public void testUdpCreateServer() throws Exception{
        ServerFactory serverFactory = new ServerFactory();
        SipServer sipServer = null;

        SipMessageHandler<String, String> firstHandler=(msg)->{
            return msg + "\n First handler!";
        };

        SipMessageHandler<String, String> secondHandler=(msg)->{
            return msg + "\n Second handler!!!";
        };

        sipServer=serverFactory
                .host(host).port(port).transport(Transport.UDP)
                .sipMessageHandler(firstHandler)
                .sipMessageHandler(secondHandler)
                .build();

        sipServer.runSync();
    }

}
