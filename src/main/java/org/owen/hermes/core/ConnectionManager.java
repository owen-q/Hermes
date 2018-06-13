package org.owen.hermes.core;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dongqlee on 2018. 5. 5..
 */
public class ConnectionManager {
    private Logger logger= LoggerFactory.getLogger(ConnectionManager.class);
    // TODO: change ChannelHandlerContext to ChannelFuture?
    private Map<String, ChannelHandlerContext> clientMap=null;

    private final int MAX_CONNECTION=10000;
    private ConnectionManager() {
        this.clientMap=new ConcurrentHashMap<>(MAX_CONNECTION);
    }

    public static ConnectionManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void addConnection(String host, int port, String transport, ChannelHandlerContext channelHandlerContext){
        String key= createConnectionKey(host, port, transport);

        this.clientMap.put(key, channelHandlerContext);

        if(logger.isDebugEnabled())
            logger.debug("Add Client :: " + key);
    }

    public ChannelHandlerContext getConnection(String host, int port, String transport){
        String key= createConnectionKey(host, port, transport);

        return this.clientMap.get(key);
    }

    public void deleteConnection(String host, int port, String transport){
        String key= createConnectionKey(host, port, transport);

        if(this.clientMap.containsKey(key)){
            logger.info("Current Clients:: {}", this.clientMap.size());

            this.clientMap.remove(key);
            if(logger.isDebugEnabled())
                logger.debug("Delete Client success :: {}", key);
        }
        else{
            if(logger.isDebugEnabled())
                logger.info("Delete Client fail :: {}", key);
        }
    }

    private String createConnectionKey(String host, int port, String transport){
//        return new StringBuilder().append(host).append(":").append(port).toString();
        return String.format("%s:%d:%s", host, port, transport);
    }


    private static class SingletonHolder{
        private static final ConnectionManager INSTANCE=new ConnectionManager();
    }
}
