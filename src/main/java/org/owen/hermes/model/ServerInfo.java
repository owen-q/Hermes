package org.owen.hermes.model;

/**
 * Created by owen_q on 2018. 5. 20..
 */
public class ServerInfo {
    private String host;
    private int port;
    private Transport transport;

    public ServerInfo(String host, int port, Transport transport) {
        this.host = host;
        this.port = port;
        this.transport = transport;
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
