package org.owen.hermes.bootstrap.handler;

import org.owen.hermes.bootstrap.HermesMessageConverter;
import org.owen.hermes.bootstrap.SipMessageConsumer;
import org.owen.hermes.bootstrap.SipMessageHandler;
import org.owen.hermes.core.ConnectionManager;
import org.owen.hermes.model.Transport;
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
public abstract class HermesAbstractSipHandler<INBOUND extends NettyInbound, OUTBOUND extends NettyOutbound>
        implements BiFunction<INBOUND, OUTBOUND, Publisher<Void>> {

    private Logger logger = LoggerFactory.getLogger(HermesAbstractSipHandler.class);

    private ConnectionManager connectionManager = null;

    private List<SipMessageHandler> sipMessageHandlerList = null;
    private SipMessageConsumer sipMessageConsumer = null;

    private HermesMessageConverter hermesMessageConverter = null;

    protected HermesAbstractSipHandler() {
    }

    protected HermesAbstractSipHandler(List<SipMessageHandler> sipMessageHandlerList, SipMessageConsumer sipMessageConsumer) {
        this.sipMessageHandlerList = sipMessageHandlerList;
        this.sipMessageConsumer = sipMessageConsumer;
        this.connectionManager = ConnectionManager.getInstance();
        this.hermesMessageConverter = HermesMessageConverter.getInstance();
    }

    public static HermesAbstractSipHandler create(Transport transport, List<SipMessageHandler> sipMessageHandlerList, SipMessageConsumer sipMessageConsumer){
        if(transport.equals(Transport.WS) || transport.equals(Transport.WSS))
            return new HermesHttpSipHandler(sipMessageHandlerList, sipMessageConsumer);
        else if(transport.equals(Transport.UDP))
            // TODO;
            System.out.println("Todo implement NettyUdpSipHandler");

        return new HermesChannelSipHandler(sipMessageHandlerList, sipMessageConsumer);
    }

    protected Flux<String> receiveString(INBOUND inbound){
        return inbound.receive().asString();
    }

    // TODO: Change input parameter to {@link org.owen.hermes.sip.wrapper.message.DefaultSipMessage}
    protected Publisher<Void> chain(INBOUND inbound){
        Flux<String> stringFlux = inbound.receive().asString();

        for(int idx=0; idx<sipMessageHandlerList.size(); idx++){
            stringFlux = stringFlux.map(this.sipMessageHandlerList.get(idx));
        }

        if(this.sipMessageConsumer != null)
            stringFlux.subscribe(this.sipMessageConsumer);
        else
            stringFlux.subscribe(PrintConsumer.INSTANCE);

        return Flux.never();
    }

    @Override
    public <V> BiFunction<INBOUND, OUTBOUND, V> andThen(Function<? super Publisher<Void>, ? extends V> after) {
        System.out.println("And then ~?");
        return null;
    }
}
