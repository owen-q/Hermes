package org.owen.proxy.sip.pro_process;

import gov.nist.javax.sip.header.Via;
import org.owen.hermes.model.ServerInfo;
import org.owen.proxy.core.Message;
import org.owen.proxy.core.ProcessState;
import org.owen.proxy.core.ProxyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 5. 1..
 */
public class ProxyPostHandler implements ProxyHandler {
    private Logger logger= LoggerFactory.getLogger(ProxyPostHandler.class);

    private String host="";
    private int port=0;
    private String transport="";
    private Via proxyVia=null;

    public ProxyPostHandler(ServerInfo serverInfo) {
        this.host=serverInfo.getHost();
        this.port=serverInfo.getPort();
        this.transport=serverInfo.getTransport().getValue();

        generateProxyVia();
    }

    public void setup(ServerInfo serverInfo){

    }

    private void generateProxyVia(){
        try{
            proxyVia=new Via();
            proxyVia.setPort(port);
            proxyVia.setHost(host);
            proxyVia.setReceived(host);
            proxyVia.setTransport(transport);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Message handle(Message message) {
        // TODO:
        if(message.getProcessState() != ProcessState.POST)
            return message;

        try{
            if(message.getValidation().isValidate()){
                message.getNewMessage().send();
            }
            else{
                // generate SipResponse using validation reason
                logger.error("Invalid message\n{}", message.getNewMessage());
            }



            if(logger.isInfoEnabled())
                logger.info("[SENT]\n{}", message.getNewMessage());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return message;
    }
}
