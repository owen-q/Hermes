package org.owen.hermes.core;

import org.owen.hermes.sip.wrapper.message.DefaultSipMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by owen_q on 2018. 6. 16..
 */
public class HermesMessage {
    private Logger logger = LoggerFactory.getLogger(HermesMessage.class);


    private DefaultSipMessage defaultSipMessage = null;

    private Map<String, Object> attributes = null;

    public HermesMessage(DefaultSipMessage defaultSipMessage) {
        this.defaultSipMessage = defaultSipMessage;
    }

    public void addAttribute(String key, Object value){
        this.attributes.put(key, value);
    }
}
