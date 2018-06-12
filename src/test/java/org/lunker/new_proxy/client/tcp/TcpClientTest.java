package org.lunker.new_proxy.client.tcp;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lunker.new_proxy.Bootstrap;
import org.lunker.new_proxy.client.TcpConnectionGenerator;
import org.lunker.new_proxy.config.Configuration;
import org.lunker.new_proxy.exception.BootstrapException;
import org.lunker.new_proxy.model.Transport;
import org.lunker.proxy.sip.SipServletImpl;

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