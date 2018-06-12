package org.lunker.new_proxy.sip.wrapper.message.lb;

import gov.nist.javax.sip.message.SIPMessage;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipRequest;

/**
 * Created by dongqlee on 2018. 4. 28..
 */
public class LoadBalancerRequest extends DefaultSipRequest {



    public LoadBalancerRequest(SIPMessage sipMessage) {
        super(sipMessage);
    }


    public void addHeader(){

    }
}
