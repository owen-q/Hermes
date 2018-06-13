package org.owen.hermes.sip.wrapper.message;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPResponse;

/**
 * Created by dongqlee on 2018. 4. 28..
 */
public class DefaultSipResponse extends DefaultSipMessage {
    private SIPResponse sipResponse =null;

    public DefaultSipResponse(SIPMessage sipMessage) {
        super(sipMessage);

        this.sipResponse =(SIPResponse) this.message;
    }

    public int getStatusCode(){
        return sipResponse.getStatusCode();
    }

    public void removeTopVia(){
        this.sipResponse.getViaHeaders().removeFirst();
    }
}
