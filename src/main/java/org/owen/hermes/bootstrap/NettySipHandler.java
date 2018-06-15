package org.owen.hermes.bootstrap;

import org.owen.hermes.core.ConnectionManager;
import org.owen.hermes.util.lambda.PrintConsumer;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.ipc.netty.NettyInbound;
import reactor.ipc.netty.NettyOutbound;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by owen_q on 2018. 6. 14..
 */
public class NettySipHandler<INBOUND extends NettyInbound, OUTBOUND extends NettyOutbound>
        implements BiFunction<INBOUND, OUTBOUND, Publisher<Void>> {

    private Logger logger = LoggerFactory.getLogger(NettySipHandler.class);
    private ConnectionManager connectionManager = null;
    private List<SipMessageHandler> sipMessageHandlerList = null;
    private SipMessageHandler chainedSipMessageHandler = null;

    private NettySipHandler(List<SipMessageHandler> sipMessageHandlerList) {
        this.sipMessageHandlerList = sipMessageHandlerList;
        this.connectionManager = ConnectionManager.getInstance();
    }

    public static NettySipHandler create(List<SipMessageHandler> sipMessageHandlerList){
        return new NettySipHandler(sipMessageHandlerList);
    }

    // 여기가 원래 람다가 구현되는 부분 ...
    // Connection 생기면 1번만 수행된다
    @Override
    public Publisher<Void> apply(INBOUND inbound, OUTBOUND outbound) {
        System.out.println("In NettyHandler apply()");

        Flux<String> stringFlux = inbound.receive().asString();

        for(int idx=0; idx<sipMessageHandlerList.size(); idx++){
            stringFlux = stringFlux.map(this.sipMessageHandlerList.get(idx));
        }

        stringFlux.subscribe(PrintConsumer.INSTANCE);

        return Flux.never();
    }

    @Override
    public <V> BiFunction<INBOUND, OUTBOUND, V> andThen(Function<? super Publisher<Void>, ? extends V> after) {
        System.out.println("And then ~?");
        return null;
    }
}
