package org.owen.proxy.sip.process.response;

import org.owen.proxy.core.Message;
import org.owen.proxy.core.ProxyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 5. 24..
 */
public class ProxyResponseHandler implements ProxyHandler {
    private Logger logger=LoggerFactory.getLogger(ProxyResponseHandler.class);

    @Override
    public Message handle(Message message) {
        // ?

        // just forward


        return message;
    }
}
