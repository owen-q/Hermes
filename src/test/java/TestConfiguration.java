import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;
import org.owen.hermes.config.Configuration;
import org.owen.hermes.exception.InvalidConfigurationException;

/**
 * Created by dongqlee on 2018. 4. 26..
 */
public class TestConfiguration {

    @Test
    public void loadConfig(){
        JsonObject config=Configuration.getInstance().get();
        Assert.assertEquals("org/lunker/proxy", config.get("type").getAsString());
    }

    @Test
    public void testValidate() throws InvalidConfigurationException {
    }
}
