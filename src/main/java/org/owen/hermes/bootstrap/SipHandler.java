package org.owen.hermes.bootstrap;

import org.owen.hermes.sip.wrapper.message.DefaultSipMessage;

import java.util.function.Function;

/**
 * Created by owen_q on 2018. 6. 13..
 */
@FunctionalInterface
public interface SipHandler extends Function<DefaultSipMessage, DefaultSipMessage>{

    /*
    // TODO: deprecated
    SipHandler INSTANCE = (msg) -> {return "Empty instance";};

    static SipHandler empty(){
        return SipHandler.INSTANCE;
    }
    */

}
