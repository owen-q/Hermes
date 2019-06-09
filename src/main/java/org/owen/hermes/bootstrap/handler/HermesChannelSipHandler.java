package org.owen.hermes.bootstrap.handler;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.owen.hermes.bootstrap.SipConsumer;
import org.owen.hermes.bootstrap.SipHandler;
import org.reactivestreams.Publisher;

import reactor.ipc.netty.NettyInbound;
import reactor.ipc.netty.NettyOutbound;

/**
 * Created by owen_q on 2018. 6. 17..
 */
@Slf4j
public class HermesChannelSipHandler extends HermesAbstractSipHandler<NettyInbound, NettyOutbound> {
    HermesChannelSipHandler(List<SipHandler> sipHandlerList, SipConsumer sipConsumer) {
        super(sipHandlerList, sipConsumer);
    }

    @Override
    public Publisher<Void> apply(NettyInbound nettyInbound, NettyOutbound nettyOutbound) {
        log.debug("In apply()");
        return chain(nettyInbound);
    }
}
