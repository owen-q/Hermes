package org.lunker.new_proxy.sip.session.sas;

import org.lunker.new_proxy.exception.InvalidArgumentException;
import org.lunker.new_proxy.sip.session.ss.SipSessionKey;
import org.lunker.new_proxy.stub.session.sas.SipApplicationSession;
import org.lunker.new_proxy.stub.session.ss.SipSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Created by dongqlee on 2018. 3. 20..
 */
public class SipApplicationSessionImpl implements SipApplicationSession {

    private Logger logger= LoggerFactory.getLogger(SipApplicationSessionImpl.class);
    private SipApplicationSessionKey sipApplicationSessionKey=null;
    private LocalDateTime createdTime=null;

    private HashMap<SipSessionKey, SipSession> sipSessions;

    private SipApplicationSessionImpl() {
    }

    public SipApplicationSessionImpl(SipApplicationSessionKey sipApplicationSessionKey) throws InvalidArgumentException{
        if(sipApplicationSessionKey==null){
            throw new InvalidArgumentException("Not valid sipApplicationSessionKey is entered");
        }

        this.sipApplicationSessionKey=sipApplicationSessionKey;
        this.sipSessions=new HashMap<>();
    }

    public void addSession(SipSession sipSession){
        this.sipSessions.put(sipSession.getSipSessionkey(), sipSession);
    }

    @Override
    public SipApplicationSessionKey getSipApplicationKey() {
        return this.sipApplicationSessionKey;
    }

    @Override
    public void addSipSession(SipSession sipSession) {
        this.sipSessions.put(sipSession.getSipSessionkey(), sipSession);
    }
}
