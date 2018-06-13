package org.owen.hermes.sip.session;

import gov.nist.javax.sip.message.SIPMessage;
import io.netty.channel.ChannelHandlerContext;
import org.owen.hermes.exception.InvalidArgumentException;
import org.owen.hermes.sip.session.sas.SipApplicationSessionImpl;
import org.owen.hermes.sip.session.sas.SipApplicationSessionKey;
import org.owen.hermes.sip.session.ss.SipSessionImpl;
import org.owen.hermes.sip.session.ss.SipSessionKey;
import org.owen.hermes.sip.wrapper.message.proxy.ProxySipMessage;
import org.owen.hermes.stub.session.SipSessionManager;
import org.owen.hermes.stub.session.sas.SipApplicationSession;
import org.owen.hermes.stub.session.ss.SipSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dongqlee on 2018. 3. 20..
 */
public class SipSessionManagerImpl implements SipSessionManager {

    private Logger logger= LoggerFactory.getLogger(SipSessionManagerImpl.class);
    private int INITIAL_CAPACITY=1024;
    private ConcurrentHashMap<SipSessionKey, SipSession> sipSessionConcurrentHashMap;
    private ConcurrentHashMap<SipApplicationSessionKey, SipApplicationSession> sipApplicationSessionConcurrentHashMap;

    public SipSessionManagerImpl() {
        this.sipApplicationSessionConcurrentHashMap=new ConcurrentHashMap<>(INITIAL_CAPACITY * 2);
        this.sipSessionConcurrentHashMap=new ConcurrentHashMap<>(INITIAL_CAPACITY);
    }

    public SipSession createOrGetSIPSession(ChannelHandlerContext ctx, ProxySipMessage proxySipMessage) {
        return createOrGetSIPSession(ctx, proxySipMessage.getRawSipMessage());
    }

    public SipSession createOrGetSIPSession(ChannelHandlerContext ctx, SIPMessage generalSipMessage) {
        // create SipSession
        // using SIPmessage

        SipSessionKey currentSipSessionKey =null;
        SipSession currentSipSession =null;

        String fromTag=generalSipMessage.getFrom().getTag();
        String toTag=generalSipMessage.getTo().getTag();

        SipApplicationSession currentCallSipApplicationSession=null;
        currentCallSipApplicationSession=findSipApplicationSession(fromTag, toTag);
        logger.info(String.format("FromTag: %s, ToTag: %s", fromTag, toTag));

        if(currentCallSipApplicationSession==null){
            // first comming request

            // create SAS && SS
            currentCallSipApplicationSession=createSipApplicationSession();
            logger.info(String.format("Create SAS : %s", currentCallSipApplicationSession.getSipApplicationKey().getGeneratedKey()));
        }

        currentSipSessionKey =new SipSessionKey(generalSipMessage, currentCallSipApplicationSession.getSipApplicationKey().getGeneratedKey());
        currentSipSession=this.sipSessionConcurrentHashMap.get(currentSipSessionKey);

        if(currentSipSession==null) {
            currentSipSession = new SipSessionImpl(currentSipSessionKey, currentCallSipApplicationSession.getSipApplicationKey());
            // Add SipSession as child of SAS
            currentCallSipApplicationSession.addSipSession(currentSipSession);
            this.sipSessionConcurrentHashMap.put(currentSipSessionKey, currentSipSession);
            currentSipSession.setCtx(ctx);
        }

        return currentSipSession;
    }

    public SipApplicationSession findSipApplicationSession(String fromTag, String toTag){
        SipApplicationSession sipApplicationSession=null;

        sipApplicationSession=findSipApplicationSession(fromTag);

        if(sipApplicationSession==null)
            sipApplicationSession=findSipApplicationSession(toTag);

        return sipApplicationSession;
    }

    public SipApplicationSession findSipApplicationSession(SipApplicationSessionKey sipApplicationSessionKey){
        return this.sipApplicationSessionConcurrentHashMap.get(sipApplicationSessionKey);
    }

    public SipApplicationSession findSipApplicationSession(SipSessionKey sipSessionKey){
        SipApplicationSessionKey sipApplicationSessionKey=new SipApplicationSessionKey(sipSessionKey.getApplicationSessionId());

        return this.sipApplicationSessionConcurrentHashMap.get(sipApplicationSessionKey);
    }

    private SipApplicationSession findSipApplicationSession(String tag){
        String sasId="";

        if(tag == null){
            return null;
        }

        String[] fromTags=tag.split("_");

        if(fromTags.length>1) {
            sasId = fromTags[fromTags.length - 1];
            return sipApplicationSessionConcurrentHashMap.get(new SipApplicationSessionKey(sasId));
        }
        else
            return null;
    }

    public SipApplicationSession createSipApplicationSession() {
        SipApplicationSessionKey sipApplicationSessionKey=new SipApplicationSessionKey();
        SipApplicationSession sipApplicationSession=null;

        try{
            sipApplicationSession=new SipApplicationSessionImpl(sipApplicationSessionKey);
            this.sipApplicationSessionConcurrentHashMap.put(sipApplicationSessionKey, sipApplicationSession);
        }
        catch (InvalidArgumentException iae){
            iae.printStackTrace();
        }

        return sipApplicationSession;
    }

    public SipSession getSipSession(SipSessionKey sipSessionKey){
        return sipSessionConcurrentHashMap.get(sipSessionKey);
    }

    public SipSession getSipSession(ProxySipMessage proxySipMessage){
        return this.getSipSession(proxySipMessage.getSipSessionKey());
    }
}
