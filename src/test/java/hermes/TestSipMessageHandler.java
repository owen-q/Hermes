package hermes;

import org.owen.hermes.model.ServerInfo;
import org.owen.hermes.sip.wrapper.message.DefaultSipMessage;
import org.owen.hermes.stub.SipMessageHandler;

import java.util.Optional;

/**
 * Created by dongqlee on 2018. 6. 12..
 */
public class TestSipMessageHandler {


}

class SipMessageHandlerImpl extends SipMessageHandler{

    public SipMessageHandlerImpl(ServerInfo serverInfo) {
        super(serverInfo);
    }

    @Override
    public void handle(Optional<DefaultSipMessage> maybeDefaultSipMessage) {

    }
}
