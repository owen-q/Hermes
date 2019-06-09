package org.owen.hermes.core;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

import org.owen.hermes.model.Transport;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by owen_q on 2018. 5. 5..
 */
@Slf4j
public class ConnectionManager {
    // TODO: change ChannelHandlerContext to {@link org.owen.hermes.core.ClientConnection}
    private Map<String, ChannelHandlerContext> clientMap = null;

    // TODO: Using configuration value
    private final int MAX_CONNECTION_NUM = 10000;
    private ConnectionManager() {
        this.clientMap = new ConcurrentHashMap<>(MAX_CONNECTION_NUM);
    }

    public static ConnectionManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void addConnection(ChannelHandlerContext channelHandlerContext){
        String connectionKey = "";
        connectionKey = createConnectionKey(channelHandlerContext);

        this.clientMap.put(connectionKey, channelHandlerContext);
    }

    public boolean isConnectionExist(String host, int port, String transport){
        String connectionKey = createConnectionKey(host, port, transport);
        return this.clientMap.containsKey(connectionKey);
    }

    public void deleteConnection(ChannelHandlerContext channelHandlerContext){
        String connectionKey = createConnectionKey(channelHandlerContext);

        if(this.clientMap.containsKey(connectionKey)){
            this.clientMap.remove(connectionKey);

            if(log.isDebugEnabled())
                log.debug("Delete Client success :: {}", connectionKey);
        }
        else{
            if(log.isDebugEnabled())
                log.info("Delete Client fail :: {}", connectionKey);
        }
    }

    public ChannelHandlerContext getConnection(String host, int port, String transport){
        String key= createConnectionKey(host, port, transport);

        return this.clientMap.get(key);
    }

    public String createConnectionKey(ChannelHandlerContext channelHandlerContext){
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
        String remoteHost = "";
        int remotePort = 0;
        String transport = channelHandlerContext.name();

        remoteHost = inetSocketAddress.getHostString();
        remotePort = inetSocketAddress.getPort();

        return new StringBuilder().append(remoteHost).append(":").append(remotePort).append(":").append(transport).toString();
    }

    private String createConnectionKey(String host, int port, String transport){
        return String.format("%s:%d:%s", host, port, transport);
    }

    private String createConnectionKey(String host, int port, Transport transport){
        return String.format("%s:%d:%s", host, port, transport.getValue());
    }

    private static class SingletonHolder{
        private static final ConnectionManager INSTANCE = new ConnectionManager();
    }
}
