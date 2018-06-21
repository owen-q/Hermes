package hermes.bootstrap.handler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.owen.hermes.bootstrap.ServerStarterElement;
import org.owen.hermes.bootstrap.handler.HermesAbstractSipHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by owen_q on 2018. 6. 19..
 */
public class TestHermesAbstractSipHandler {
    private Logger logger = LoggerFactory.getLogger(TestHermesAbstractSipHandler.class);



    @Before
    public void beforeTest() {
        // Mock

    }

    @After
    public void afterTest() {


    }

    @Test
    public void test() {

        // Given:

        // When:
        HermesAbstractSipHandler mockHermesAbstractSipHandler = Mockito.mock(HermesAbstractSipHandler.class);
        ServerStarterElement mockServerStarterElement = Mockito.mock(ServerStarterElement.class);


        System.out.println("break");
        // Then:

    }
}
