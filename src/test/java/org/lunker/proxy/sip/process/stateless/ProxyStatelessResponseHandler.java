package org.lunker.proxy.sip.process.stateless;

import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipResponse;
import org.lunker.proxy.core.Message;
import org.lunker.proxy.core.ProxyHandler;
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
