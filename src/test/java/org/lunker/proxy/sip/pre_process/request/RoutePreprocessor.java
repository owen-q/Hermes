package org.lunker.proxy.sip.pre_process.request;

import gov.nist.javax.sip.header.Route;
import gov.nist.javax.sip.header.RouteList;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipRequest;
import org.lunker.proxy.core.Message;
import org.lunker.proxy.core.ProxyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 5. 21..
 */
public class RoutePreprocessor implements ProxyHandler{
    private Logger logger= LoggerFactory.getLogger(RoutePreprocessor.class);

    @Override
    public Message handle(Message message) {
        logger.info("In RoutePreprocessor");
        removeRouteHeader(message);

        return message;
    }

    // TODO: test
    public RoutePreprocessor removeRouteHeader(Message message){
        DefaultSipRequest defaultSipRequest=null;

        defaultSipRequest=(DefaultSipRequest) message.getOriginalMessage();

        RouteList routeHeader=defaultSipRequest.getRouteHeaders();

        if(routeHeader!=null){
            Route lastRouteHeader=(Route) routeHeader.getLast();
        }

        return this;
    }
}
