package org.owen.hermes.model;

/**
 * Created by dongqlee on 2018. 5. 20..
 */
public enum Transport {
    NONE("none"),
    TCP("tcp"),
    TLS("tls"),
    UDP("udp"),
    WS("ws"),
    WSS("wss");

    private String value;

    Transport(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Transport{" +
                "value='" + value + '\'' +
                '}';
    }
}
