package org.lunker.new_proxy.model;

import org.lunker.new_proxy.core.constants.ServerType;

/**
 * Created by dongqlee on 2018. 5. 20..
 */
public class ServerInfo {
    private ServerType serverType;
    private String host;
    private int port;
    private Transport transport;

    public ServerInfo(ServerType serverType,String host, int port, Transport transport) {
        this.serverType=serverType;
        this.host = host;
        this.port = port;
        this.transport = transport;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }
}
