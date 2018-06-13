package org.owen.hermes.stub.session.ss;

import io.netty.channel.ChannelHandlerContext;
import org.owen.hermes.sip.session.ss.SipSessionKey;
import org.owen.hermes.sip.wrapper.message.proxy.ProxySipRequest;
import org.owen.hermes.stub.session.sas.SipApplicationSession;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public interface SipSession {
    SipApplicationSession getSipApplicationSession();

    /**
     * Get {@link SipSessionKey}
     * @return
     */
    SipSessionKey getSipSessionkey();

    /**
     * Set Session Attribute
     * @param key
     * @param value
     */
    void setAttribute(String key, Object value);

    /**
     * Get Session Attribute
     * @param key
     * @return
     */
    Object getAttribute(String key);

    /**
     * Create DefaultSipRequest
     * @param method
     * @return
     */
    ProxySipRequest createRequest(String method);

    void setFirstRequest(ProxySipRequest generalSipRequest);

    ProxySipRequest getFirstRequest();

    void setCtx(ChannelHandlerContext ctx);

    ChannelHandlerContext getCtx();
}
