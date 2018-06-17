package org.owen.hermes.bootstrap;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.ipc.netty.http.server.HttpServerRequest;
import reactor.ipc.netty.http.server.HttpServerResponse;

import java.util.function.BiFunction;

/**
 * Created by owen_q on 2018. 6. 17..
 */
public class NettyHttpSipHandler extends NettySipHandler<HttpServerRequest, HttpServerResponse> {
    private Logger logger = LoggerFactory.getLogger(NettyHttpSipHandler.class);

    public NettyHttpSipHandler() {
    }


}
