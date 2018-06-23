package org.owen.hermes.bootstrap.handler;

import org.owen.hermes.bootstrap.SipConsumer;
import org.owen.hermes.bootstrap.SipHandler;
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

    HermesChannelSipHandler(List<SipHandler> sipHandlerList, SipConsumer sipConsumer) {
        super(sipHandlerList, sipConsumer);
    }

    @Override
    public Publisher<Void> apply(NettyInbound nettyInbound, NettyOutbound nettyOutbound) {
        logger.debug("In apply()");
        return chain(nettyInbound);
    }
}
