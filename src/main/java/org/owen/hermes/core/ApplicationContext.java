package org.owen.hermes.core;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dongqlee on 2018. 5. 5..
 */
public class ApplicationContext {
    private Logger logger= LoggerFactory.getLogger(ApplicationContext.class);
    private Map<String, ChannelHandlerContext> clientMap=null;

    public ApplicationContext() {
        this.clientMap=new ConcurrentHashMap<>();
    }




}
