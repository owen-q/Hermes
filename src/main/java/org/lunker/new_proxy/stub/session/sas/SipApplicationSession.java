package org.lunker.new_proxy.stub.session.sas;


import org.lunker.new_proxy.sip.session.sas.SipApplicationSessionKey;
import org.lunker.new_proxy.stub.session.ss.SipSession;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public interface SipApplicationSession {

    SipApplicationSessionKey getSipApplicationKey();

    void addSipSession(SipSession sipSession);
}
