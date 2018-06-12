package org.lunker.new_proxy.sip.wrapper.message.lb;

import gov.nist.javax.sip.message.SIPMessage;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipResponse;

/**
 * Created by dongqlee on 2018. 4. 28..
 */
public class LoadBalancerResponse extends DefaultSipResponse {

    public LoadBalancerResponse(SIPMessage sipMessage) {
        super(sipMessage);
    }
}
