package org.lunker.proxy.model;

import org.lunker.new_proxy.model.Transport;

/**
 * Created by dongqlee on 2018. 5. 18..
 */
public class RemoteAddress {
    private Transport transport;
    private String host;
    private int port;

    public RemoteAddress(Transport transport, String host, int port) {
        this.transport = transport;
        this.host = host;
        this.port = port;
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

    @Override
    public String toString() {
        return "RemoteAddress{" +
                "transport=" + transport +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
