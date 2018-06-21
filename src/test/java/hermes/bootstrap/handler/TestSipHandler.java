package hermes.bootstrap.handler;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.owen.hermes.bootstrap.SipMessageHandler;
import org.owen.hermes.stub.SipServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * Created by owen_q on 2018. 6. 14..
 */
public class TestSipHandler {
    private Logger logger = LoggerFactory.getLogger(TestSipHandler.class);
    private SipServer sipServer = null;

    private SipMessageHandler sampleSipMessageHandler = null;

    @Before
    public void beforeTest(){

    }

    @Test
    public void testCreateSipMessageHandlerImpl(){
        Mono<String> testMono=Mono.just("ping");

        // Then Implementation
        SipMessageHandler<String, String> tmp = arg -> "pong";

        testMono.map(tmp).subscribe(output -> Assert.assertEquals("pong", output));
    }

    @After
    public void afterTest(){

    }
}
