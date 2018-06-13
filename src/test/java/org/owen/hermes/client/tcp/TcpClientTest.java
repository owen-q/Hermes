package org.owen.hermes.client.tcp;

import org.junit.BeforeClass;
import org.junit.Test;
import org.owen.hermes.Bootstrap;
import org.owen.hermes.client.TcpConnectionGenerator;
import org.owen.hermes.config.Configuration;
import org.owen.hermes.exception.BootstrapException;
import org.owen.hermes.model.Transport;
import org.owen.proxy.sip.SipServletImpl;

import static org.junit.Assert.*;

public class TcpClientTest {
    @Test
    public void connect() throws Exception {
        Bootstrap b = new Bootstrap();
        b.addHandler(Transport.TCP, SipServletImpl.class);
        TcpConnectionGenerator tcpConnectionGenerator = TcpConnectionGenerator.getInstance();
        tcpConnectionGenerator.generate("127.0.0.1", 10010, SipServletImpl.class);
    }

    @Test
    public void disconnect() {
    }
}