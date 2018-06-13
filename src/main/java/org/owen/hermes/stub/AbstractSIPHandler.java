package org.owen.hermes.stub;

import org.owen.hermes.sip.wrapper.message.DefaultSipMessage;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
@Deprecated
public interface AbstractSIPHandler {
    DefaultSipMessage handleRegister(DefaultSipMessage registerRequest);
    DefaultSipMessage handleInvite(DefaultSipMessage inviteRequest);
    DefaultSipMessage handleCancel(DefaultSipMessage cancelRequest);
    DefaultSipMessage handleAck(DefaultSipMessage ackRequest);
    DefaultSipMessage handleBye(DefaultSipMessage byeRequest);
}
