package org.owen.hermes.bootstrap;

import org.owen.hermes.sip.wrapper.message.DefaultSipMessage;

import java.util.function.Consumer;

/**
 * Created by owen_q on 2018. 6. 16..
 */
public interface SipConsumer extends Consumer<DefaultSipMessage> {

    void send(DefaultSipMessage defaultSipMessage);
}
