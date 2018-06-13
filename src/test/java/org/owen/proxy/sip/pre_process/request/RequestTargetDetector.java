package org.owen.proxy.sip.pre_process.request;

import org.owen.proxy.core.Message;
import org.owen.proxy.core.ProxyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 5. 21..
 */
public class RequestTargetDetector implements ProxyHandler {
    private Logger logger= LoggerFactory.getLogger(RequestTargetDetector.class);


    // TODO:
    @Override
    public Message handle(Message message) {
        logger.info("In RequestTargetDetector");
        return message;
    }
}
