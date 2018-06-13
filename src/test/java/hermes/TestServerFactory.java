package hermes;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.owen.hermes.bootstrap.ServerFactory;
import org.owen.hermes.model.Transport;
import org.owen.hermes.stub.SipServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 6. 13..
 */
public class TestServerFactory {
    private Logger logger = LoggerFactory.getLogger(TestServerFactory.class);
    private String host="localhost";
    private int port=10202;

    @Before
    public void beforeTest(){

    }

    @After
    public void afterTest(){

    }

    @Test
    public void testCreateServer(){
        ServerFactory serverFactory=new ServerFactory();
        SipServer sipServer=null;

        serverFactory=serverFactory.host(host).port(port).transport(Transport.TCP);

        sipServer=serverFactory.build();

        Assert.assertNotNull(sipServer);
    }

    @Test
    public void testRunServerAsync() throws Exception{
        SipServer sipServer = null;
        sipServer.run(false);
    }

    @Test
    public void testRunServerSync() throws Exception{
        SipServer sipServer = null;
        sipServer.run(false);
    }
}
