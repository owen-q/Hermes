package org.owen.proxy.sip.process.stateless;

import org.owen.hermes.sip.wrapper.message.proxy.ProxySipResponse;
import org.owen.proxy.core.Message;
import org.owen.proxy.core.ProxyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 5. 24..
 */
public class ProxyStatelessResponseHandler implements ProxyHandler {
    private Logger logger= LoggerFactory.getLogger(ProxyStatelessResponseHandler.class);

    @Override
    public Message handle(Message message) {
        message.setNewMessage(  (ProxySipResponse) ((ProxySipResponse) message.getOriginalMessage()).clone());

        return message;
    }


}
