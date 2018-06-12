package org.lunker.new_proxy.core;

import gov.nist.javax.sip.message.SIPMessage;
import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.sip.session.SipSessionManagerImpl;
import org.lunker.new_proxy.sip.session.sas.SipApplicationSessionKey;
import org.lunker.new_proxy.sip.session.ss.SipSessionKey;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipMessage;
import org.lunker.new_proxy.stub.session.sas.SipApplicationSession;
import org.lunker.new_proxy.stub.session.ss.SipSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 20..
 */
@Deprecated
public class ProxyContext {

    private Logger logger= LoggerFactory.getLogger(ProxyContext.class);
    private static ProxyContext instance=null;
    private SipSessionManagerImpl sipSessionManager=null;
//    private ConcurrentHashMap<String, ChannelHandlerContext> clientMap=null;
//    private Registrar registrar=null;

    private ProxyContext() {
        this.sipSessionManager=new SipSessionManagerImpl();
//        this.registrar=Registrar.getInstance();
    }

    public static ProxyContext getInstance() {
        if (instance==null)
            instance=new ProxyContext();
        return instance;
    }

    public SipSession createOrGetSIPSession(ChannelHandlerContext ctx, ProxySipMessage proxySipMessage){
        return sipSessionManager.createOrGetSIPSession(ctx, proxySipMessage.getRawSipMessage());
    }

    public SipSession createOrGetSIPSession(ChannelHandlerContext ctx, SIPMessage generalSipMessage){
        return sipSessionManager.createOrGetSIPSession(ctx, generalSipMessage);
    }

    public SipSession getSipSession(SipSessionKey sipSessionKey){
        return sipSessionManager.getSipSession(sipSessionKey);
    }

    public SipApplicationSession getSipApplicationSession(SipApplicationSessionKey sipApplicationSessionKey){
        return sipSessionManager.findSipApplicationSession(sipApplicationSessionKey);
    }

    public SipApplicationSession getSipApplicationSession(SipSessionKey sipSessionKey){
        return sipSessionManager.findSipApplicationSession(sipSessionKey);
    }


}
