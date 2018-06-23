package org.owen.hermes.bootstrap.handler;

import org.owen.hermes.bootstrap.HermesMessageConverter;
import org.owen.hermes.bootstrap.SipConsumer;
import org.owen.hermes.bootstrap.SipHandler;
import org.owen.hermes.core.ConnectionManager;
import org.owen.hermes.model.Transport;
import org.owen.hermes.sip.wrapper.message.DefaultSipMessage;
import org.owen.hermes.sip.wrapper.message.DefaultSipMessageEmpty;
import org.owen.hermes.util.lambda.PrintConsumer;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.ipc.netty.NettyInbound;
import reactor.ipc.netty.NettyOutbound;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by owen_q on 2018. 6. 14..
 */
public abstract class HermesAbstractSipHandler<INBOUND extends NettyInbound, OUTBOUND extends NettyOutbound>
        implements BiFunction<INBOUND, OUTBOUND, Publisher<Void>> {

    private Logger logger = LoggerFactory.getLogger(HermesAbstractSipHandler.class);

    private ConnectionManager connectionManager = null;

    private List<SipHandler> sipHandlerList = null;
    private SipConsumer sipConsumer = null;

    private HermesMessageConverter hermesMessageConverter = null;

    protected HermesAbstractSipHandler() {
    }

    protected HermesAbstractSipHandler(List<SipHandler> sipHandlerList, SipConsumer sipConsumer) {
        this.sipHandlerList = sipHandlerList;
        this.sipConsumer = sipConsumer;
        this.connectionManager = ConnectionManager.getInstance();
//        this.hermesMessageConverter = HermesMessageConverter.getInstance();
        this.hermesMessageConverter = new HermesMessageConverter();
    }

    public static HermesAbstractSipHandler create(Transport transport, List<SipHandler> sipHandlerList, SipConsumer sipConsumer){
        if(transport.equals(Transport.WS) || transport.equals(Transport.WSS))
            return new HermesHttpSipHandler(sipHandlerList, sipConsumer);
        else if(transport.equals(Transport.UDP))
            // TODO;
            System.out.println("Todo implement NettyUdpSipHandler");

        // FOR TCP | TLS
        return new HermesChannelSipHandler(sipHandlerList, sipConsumer);
    }

    protected Flux<String> receiveString(INBOUND inbound){
        return inbound.receive().asString();
    }

    // TODO: Change input parameter to {@link org.owen.hermes.sip.wrapper.message.DefaultSipMessage}
    protected Publisher<Void> chain(INBOUND inbound){
        // read string
        Flux<String> readStrFromChain = inbound.receive().asString();

        // change DefaultSipMessage
        Flux<DefaultSipMessage> sipFlux = readStrFromChain.map(hermesMessageConverter::convertStringToDefaultSipMessage).filter(defaultSipMessage -> defaultSipMessage instanceof DefaultSipMessageEmpty);

        for(int idx = 0; idx< sipHandlerList.size(); idx++){
            sipFlux = sipFlux.map(this.sipHandlerList.get(idx));
        }

        if(this.sipConsumer != null)
            sipFlux.subscribe(this.sipConsumer);
        else
            sipFlux.subscribe(PrintConsumer.INSTANCE);

        return Flux.never();
    }
}
