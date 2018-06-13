package org.owen.hermes.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 20..
 */
@Deprecated
public class ProxyContext {

    private Logger logger= LoggerFactory.getLogger(ProxyContext.class);
    private static ProxyContext instance=null;
//    private ConcurrentHashMap<String, ChannelHandlerContext> clientMap=null;
//    private Registrar registrar=null;

    private ProxyContext() {
//        this.sipSessionManager=new SipSessionManagerImpl();
//        this.registrar=Registrar.getInstance();
    }

    public static ProxyContext getInstance() {
        if (instance==null)
            instance=new ProxyContext();
        return instance;
    }
}
