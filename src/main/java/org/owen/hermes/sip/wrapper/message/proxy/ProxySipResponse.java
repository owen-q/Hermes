package org.owen.hermes.sip.wrapper.message.proxy;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPResponse;
import org.owen.hermes.sip.wrapper.message.DefaultSipResponse;
import org.owen.hermes.sip.wrapper.message.Sessionable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class ProxySipResponse extends DefaultSipResponse implements Sessionable {
    private Logger logger= LoggerFactory.getLogger(ProxySipResponse.class);

    public ProxySipResponse(SIPMessage jainSipResponse) {
        super(jainSipResponse);
    }

    @Override
    public Object clone() {
        SIPResponse sipResponse=(SIPResponse) this.message.clone();
        return new ProxySipResponse(sipResponse);
    }
}