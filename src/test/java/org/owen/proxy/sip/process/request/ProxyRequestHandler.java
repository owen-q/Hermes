package org.owen.proxy.sip.process.request;

import org.owen.hermes.sip.wrapper.message.proxy.ProxySipRequest;
import org.owen.proxy.core.Message;
import org.owen.proxy.core.ProxyHandler;
import org.owen.proxy.sip.process.stateless.ProxyStatelessRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 5. 24..
 */
public class ProxyRequestHandler implements ProxyHandler{
    private Logger logger= LoggerFactory.getLogger(ProxyRequestHandler.class);

    private ProxyStatelessRequestHandler proxyStatelessRequestHandler =null;

    public ProxyRequestHandler() {
    }

    @Override
    public Message handle(Message message) {
        ProxySipRequest proxySipRequest=(ProxySipRequest) message.getOriginalMessage();
        /*
        String method=proxySipRequest.getMethod();

        if(method.equals(SIPRequest.REGISTER))
            newMessage=this.handleRegister(originalMessage);
        else if (method.equals(SIPRequest.INVITE))
            newMessage=this.handleInvite(originalMessage);
        else if(method.equals(SIPRequest.ACK))
            newMessage=this.handleAck(originalMessage);
        else if(method.equals(SIPRequest.BYE))
            newMessage=this.handleBye(originalMessage);
        */

        /*
        if(isServerSafe()){
            // run as Stateless proxy
            this.proxyStatelessRequestHandler.handle(message);
        }
        else{
            // run as Stateful proxy
            this.
        }
        */



        return message;
    }


    private boolean isServerSafe(){
        return true;
    }

}
