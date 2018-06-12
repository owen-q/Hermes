package org.lunker.proxy.core;

/**
 * Created by dongqlee on 2018. 5. 17..
 */
public interface ProxyHandler {
    Message handle(Message message);
}
