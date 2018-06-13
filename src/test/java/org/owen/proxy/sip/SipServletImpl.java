package org.owen.proxy.sip;

import org.owen.hermes.model.ServerInfo;
import org.owen.hermes.sip.wrapper.message.DefaultSipMessage;
import org.owen.hermes.stub.SipMessageHandler;
import org.owen.proxy.core.Message;
import org.owen.proxy.sip.pre_process.ProxyPreHandler;
import org.owen.proxy.sip.pro_process.ProxyPostHandler;
import org.owen.proxy.sip.process.ProxyInHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

/**
 * Created by dongqlee on 2018. 4. 25..
 */
public class SipServletImpl extends SipMessageHandler {
    private Logger logger= LoggerFactory.getLogger(SipServletImpl.class);

    private ProxyPreHandler proxyPreHandler=null;
    private ProxyInHandler proxyInHandler=null;
    private ProxyPostHandler proxyProHandler=null;

    public SipServletImpl() {
        super();
    }

    public SipServletImpl(ServerInfo serverInfo) {
        super(serverInfo);

        proxyPreHandler=new ProxyPreHandler(this.getServerInfo());
        proxyInHandler=new ProxyInHandler(this.getServerInfo());
        proxyProHandler=new ProxyPostHandler(this.getServerInfo());
    }

    @Override
    public void handle(Optional<DefaultSipMessage> maybeDefaultSipMessage) {

        // ChannelHandlerContext ;; // remote address

        // remoteAddresss -> rport, received에 넣음




        if(logger.isInfoEnabled())
            logger.info("[RECEIVED]:\n" + maybeDefaultSipMessage.get().toString());

        maybeDefaultSipMessage.ifPresent((defaultSipMessage)->{
            Message message=new Message(defaultSipMessage);

            Mono<?> proxyAsync=Mono.just(message)
                    .map(proxyPreHandler::handle)
                    .map(proxyInHandler::handle)
                    .map(proxyProHandler::handle);

            proxyAsync=proxyAsync.subscribeOn(Schedulers.immediate());
            proxyAsync.subscribe();
        });
    }
}
