package org.lunker.proxy.sip.pre_process.response;

import gov.nist.javax.sip.header.Via;
import org.lunker.new_proxy.model.ServerInfo;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipResponse;
import org.lunker.proxy.core.Message;
import org.lunker.proxy.core.ProxyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 5. 23..
 */
public class ViaRemover implements ProxyHandler{
    private Logger logger= LoggerFactory.getLogger(ViaRemover.class);
    private ServerInfo serverInfo=null;

    public ViaRemover(ServerInfo serverInfo) {
        this.serverInfo=serverInfo;
    }

    @Override
    public Message handle(Message message) {
        removeTopVia(message);

        return message;
    }

    private Message removeTopVia(Message message){
        DefaultSipResponse defaultSipResponse=(DefaultSipResponse) message.getOriginalMessage();
        Via via=defaultSipResponse.getTopmostVia();

        if(via.getHost().equalsIgnoreCase(serverInfo.getHost())){
            logger.info("Remove TopVia");
            defaultSipResponse.removeTopVia();
        }
        else{
            logger.warn("Invalid routed sip message. {}\ndrop...", defaultSipResponse);
        }

        return message;
    }

}
