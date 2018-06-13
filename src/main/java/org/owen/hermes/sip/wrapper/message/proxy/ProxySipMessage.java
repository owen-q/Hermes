package org.owen.hermes.sip.wrapper.message.proxy;

import gov.nist.javax.sip.message.SIPMessage;
import org.owen.hermes.core.ProxyContext;
import org.owen.hermes.sip.session.ss.SipSessionKey;
import org.owen.hermes.sip.util.SipMessageFactory;
import org.owen.hermes.sip.wrapper.message.DefaultSipMessage;
import org.owen.hermes.stub.session.sas.SipApplicationSession;
import org.owen.hermes.stub.session.ss.SipSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
@Deprecated
public abstract class ProxySipMessage extends DefaultSipMessage {
    protected Logger logger= LoggerFactory.getLogger(ProxySipMessage.class);

    protected SipSessionKey sipSessionKey;
    protected SipMessageFactory sipMessageFactory;

    protected Map<String, Object> attributes;

    protected ProxyContext proxyContext;

    //TODO: create pre-defined static messages
//    public static ProxySipMessage SERVER_INTERNAL_ERROR_500=new ProxySipResponse(new SIPResponse(), new SipSessionKey());

    protected ProxySipMessage() {
        this.proxyContext=ProxyContext.getInstance();
        this.sipMessageFactory=SipMessageFactory.getInstance();
    }

    protected ProxySipMessage(SIPMessage sipMessage) {
        this.message = sipMessage;
//        this.sipSessionKey = sipSessionKey;
        this.proxyContext=ProxyContext.getInstance();
        this.sipMessageFactory=SipMessageFactory.getInstance();

    }

    protected ProxySipMessage(SIPMessage message, SipSessionKey sipSessionKey) {
        this.message = message;
        this.sipSessionKey = sipSessionKey;
        this.proxyContext=ProxyContext.getInstance();
        this.sipMessageFactory=SipMessageFactory.getInstance();

        //
    }

    public SipSessionKey getSipSessionKey() {
        return sipSessionKey;
    }

    /**
     * Get SipSession
     * @return {@link SipSession}
     */
    public SipSession getSipSession(){
        return this.proxyContext.getSipSession(sipSessionKey);
    }

    public SipApplicationSession getSipApplicationSession(){
        return this.proxyContext.getSipApplicationSession(sipSessionKey);
    }
}
