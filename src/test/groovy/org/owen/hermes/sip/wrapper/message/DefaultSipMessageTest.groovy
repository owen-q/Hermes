package org.owen.hermes.sip.wrapper.message

import gov.nist.javax.sip.message.SIPMessage
import gov.nist.javax.sip.message.SIPRequest
import spock.lang.Specification

/**
 * Created by owen_q on 2018. 6. 21..
 */
class DefaultSipMessageTest extends Specification {
    private SIPMessage sipMessage = new SIPRequest();

    def "Create DefaultSipMessage instance"(){
        given:
        def mockDefaultSipMessage = Mock(DefaultSipMessage.class)



        when:
        String method = mockDefaultSipMessage.getMethod()

        then:
        method == "method"
    }

    def ""(){

    }
}
