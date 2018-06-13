package org.owen.hermes.client;

import org.owen.hermes.exception.InvalidConfigurationException;
import org.owen.hermes.sip.processor.ServerProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * interface to generate connection
 */
public interface ConnectionGenerator {
//    protected ServerProcessor serverProcessor;

    public void generate(String host, int port, Class SipMessageHandlerImpl) throws Exception;

//    private static ServerProcessor generateServerProcessor(ServerInfo)
}
