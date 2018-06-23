package org.owen.hermes.sip.wrapper.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by owen_q on 2018. 6. 23..
 */
public final class DefaultSipMessageEmpty extends DefaultSipMessage {
    private Logger logger = LoggerFactory.getLogger(DefaultSipMessageEmpty.class);

    public static DefaultSipMessageEmpty getInstance(){
        return Holder.INSTANCE;
    }




    private static class Holder{
        private static DefaultSipMessageEmpty INSTANCE = new DefaultSipMessageEmpty();
    }
}
