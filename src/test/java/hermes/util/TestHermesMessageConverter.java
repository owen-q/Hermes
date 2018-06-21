package hermes.util;

import gov.nist.javax.sip.message.SIPRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.owen.hermes.bootstrap.HermesMessageConverter;
import org.owen.hermes.model.RemoteAddress;
import org.owen.hermes.sip.wrapper.message.DefaultSipMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by owen_q on 2018. 6. 18..
 */
public class TestHermesMessageConverter {
    private Logger logger = LoggerFactory.getLogger(TestHermesMessageConverter.class);

    private HermesMessageConverter messageConverter = null;

    private String sampleRegisterMessage = "REGISTER sip:203.240.153.30:5061;transport=tcp SIP/2.0\n" +
            "Max-Forwards: 70\n" +
            "Via: SIP/2.0/TCP 10.0.1.48:5060;branch=z9hG4bK3f34e927a;rport=14324;received=218.51.112.46\n" +
            "Call-ID: ae02d002c7cecdce0151ba8eea11746a@10.0.1.48\n" +
            "From: \"sangsunkim\" <sip:sangsunkim@voiceloco.com:5061>;tag=ab4e0ebcd7bcdc3\n" +
            "To: \"sangsunkim\" <sip:sangsunkim@voiceloco.com:5061>\n" +
            "Route: <sip:203.240.153.30:5061;lr;transport=tcp>\n" +
            "CSeq: 86545 REGISTER\n" +
            "Contact: <sip:sangsunkim@10.0.1.48:5060;maddr=203.240.153.14;transport=tcp>;expires=810\n" +
            "Proxy-Require: com.nortelnetworks.firewall\n" +
            "Expires: 810\n" +
            "Authorization: Digest response=\"a32dfc1a2710424e2d9ab92001dd2c2d\",nc=0001520f,username=\"sangsunkim\",realm=\"voiceloco.com\",nonce=\"d9d0941a41e55c0c1cba9dd300059879\",algorithm=MD5,qop=auth,cnonce=\"a439f4b332a18664a8685d13841ba4a1\",uri=\"sip:203.240.153.30:5061;transport=tcp\"\n" +
            "User-Agent: Stonehenge IP255FA 1.30.398\n" +
            "Lb-Ip: 203.240.153.11\n" +
            "Lb-Port: 5072\n" +
            "Client-Ip: 218.51.112.46\n" +
            "Client-Port: 14324\n" +
            "Content-Length: 0";

    @Before
    public void beforeTest() {
        messageConverter = HermesMessageConverter.getInstance();
    }

    @After
    public void afterTest(){
        messageConverter = null;
    }

    @Test
    public void testConvertStringToDefaultSipMessage(){
        RemoteAddress remoteAddress = new RemoteAddress();

        remoteAddress.host = "10.0.1.202";
        remoteAddress.port = 10000;
        remoteAddress.transport = "tcp";

        DefaultSipMessage registerMessage = messageConverter.convertStringToDefaultSipMessage(remoteAddress, sampleRegisterMessage);

        Assert.assertEquals(SIPRequest.REGISTER, registerMessage.getMethod());
    }

}
