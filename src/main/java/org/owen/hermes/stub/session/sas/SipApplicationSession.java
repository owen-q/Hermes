package org.owen.hermes.stub.session.sas;


import org.owen.hermes.sip.session.sas.SipApplicationSessionKey;
import org.owen.hermes.stub.session.ss.SipSession;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public interface SipApplicationSession {

    SipApplicationSessionKey getSipApplicationKey();

    void addSipSession(SipSession sipSession);
}
