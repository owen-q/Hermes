package org.lunker.proxy.sip.process;

import com.google.gson.Gson;
import org.lunker.new_proxy.model.ServerInfo;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipMessage;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipRequest;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipResponse;
import org.lunker.proxy.core.Message;
import org.lunker.proxy.core.ProcessState;
import org.lunker.proxy.core.ProxyHandler;
import org.lunker.proxy.registrar.Registrar;
import org.lunker.proxy.sip.process.request.ProxyRequestHandler;
import org.lunker.proxy.sip.process.response.ProxyResponseHandler;
import org.lunker.proxy.sip.process.stateless.ProxyStatelessRequestHandler;
import org.lunker.proxy.sip.process.stateless.ProxyStatelessResponseHandler;
import org.lunker.proxy.util.JedisConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;

/**
 * Created by dongqlee on 2018. 5. 15..
 */
public class ProxyInHandler implements ProxyHandler {
    private Logger logger= LoggerFactory.getLogger(ProxyInHandler.class);

    private javax.sip.SipFactory sipFactory=null;
    private AddressFactory addressFactory=null;
    private HeaderFactory headerFactory=null;
    private MessageFactory messageFactory=null;
    private Registrar registrar=null;
    private JedisConnection jedisConnection=null;
    private Gson gson=null;

    private ProxyRequestHandler proxyRequestHandler=null;
    private ProxyResponseHandler proxyResponseHandler=null;
    private ProxyStatelessRequestHandler proxyStatelessRequestHandler=null;
    private ProxyStatelessResponseHandler proxyStatelessResponseHandler=null;

    public ProxyInHandler(ServerInfo serverInfo) {
        jedisConnection=JedisConnection.getInstance();
        gson=new Gson();

        try{
            this.sipFactory=javax.sip.SipFactory.getInstance();
            this.headerFactory=sipFactory.createHeaderFactory();
            this.registrar= Registrar.getInstance();
            this.messageFactory=sipFactory.createMessageFactory();
            this.addressFactory=sipFactory.createAddressFactory();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        proxyRequestHandler=new ProxyRequestHandler();
        proxyResponseHandler=new ProxyResponseHandler();

        proxyStatelessRequestHandler=new ProxyStatelessRequestHandler(serverInfo);
        proxyStatelessResponseHandler=new ProxyStatelessResponseHandler();
    }

    @Override
    public Message handle(Message message) {
        if(message.getProcessState() != ProcessState.IN)
            return message;

        DefaultSipMessage originalMessage=null;
        Mono<Message> messageProcessMono=null;

        originalMessage=message.getOriginalMessage();

        if(originalMessage instanceof ProxySipRequest){
            messageProcessMono=Mono.just(message)
                    .map(proxyStatelessRequestHandler::handle);
        }
        else if(originalMessage instanceof ProxySipResponse){
            messageProcessMono=Mono.just(message)
                    .map(proxyStatelessResponseHandler::handle);
        }

        messageProcessMono.subscribeOn(Schedulers.single());
        messageProcessMono.block();

        if(message.getValidation().isValidate()){
            message.setProcessState(ProcessState.POST);
        }

        return message;
    }
}
