package org.owen.hermes.bootstrap.handler;

import org.owen.hermes.bootstrap.SipMessageConsumer;
import org.owen.hermes.bootstrap.SipMessageHandler;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.ipc.netty.NettyInbound;
import reactor.ipc.netty.NettyOutbound;

import java.util.List;

/**
 * Created by owen_q on 2018. 6. 17..
 */
public class HermesChannelSipHandler extends HermesAbstractSipHandler<NettyInbound, NettyOutbound> {
    private Logger logger = LoggerFactory.getLogger(HermesChannelSipHandler.class);

    HermesChannelSipHandler(List<SipMessageHandler> sipMessageHandlerList, SipMessageConsumer sipMessageConsumer) {
        super(sipMessageHandlerList, sipMessageConsumer);
    }

    @Override
    public Publisher<Void> apply(NettyInbound nettyInbound, NettyOutbound nettyOutbound) {
        return chain(nettyInbound);
    }
}
