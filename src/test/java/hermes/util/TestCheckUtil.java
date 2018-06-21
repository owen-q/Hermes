package hermes.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.owen.hermes.util.CheckUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by owen_q on 2018. 6. 19..
 */
public class TestCheckUtil {
    private Logger logger = LoggerFactory.getLogger(TestCheckUtil.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Test
    public void testCheckNotNull() {
        // Given:
        Object givenObject = null;

        // Then:
        expectedException.expect(IllegalArgumentException.class);

        // When:
        CheckUtil.checkNotNull(givenObject, "givenObject");
    }

    @Test
    public void testCheckNotZero() {
        // Given:
        int givenNum = 0;

        // Then:
        expectedException.expect(IllegalArgumentException.class);

        // When:
        CheckUtil.checkNotZero(givenNum, "givenNum");
    }
}
