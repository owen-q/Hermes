package org.owen.hermes.bootstrap;

import java.util.function.Function;

/**
 * Created by owen_q on 2018. 6. 13..
 */
@FunctionalInterface
public interface SipMessageHandler<T, R> extends Function<T, R>{
    // TODO: deprecated
    SipMessageHandler INSTANCE = (msg) -> {return "Empty instance";};

    static SipMessageHandler empty(){
        return SipMessageHandler.INSTANCE;
    }
}
