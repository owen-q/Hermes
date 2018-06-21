package hermes.sip.message;

import org.junit.Test;
import org.mockito.Mockito;
import org.owen.hermes.sip.wrapper.message.DefaultSipMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

;

/**
 * Created by owen_q on 2018. 6. 19..
 */
public class TestDefaultSipMessage {
    private Logger logger = LoggerFactory.getLogger(TestDefaultSipMessage.class);

    @Test
    public void test() {
        DefaultSipMessage mockDefaultSipMessage = Mockito.mock(DefaultSipMessage.class);

        List testList = Mockito.mock(ArrayList.class);

        testList.add(1);

        Mockito.when(mockDefaultSipMessage.getMethod());

//        Mockito.verify(testList).size();
        Mockito.verify(testList).add(1);

        System.out.println("break");

        // Given:

        // When:

        // Then:
    }
}
