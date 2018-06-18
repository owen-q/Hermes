package org.owen.hermes.bootstrap.handler;

import org.owen.hermes.bootstrap.SipMessageConsumer;
import org.owen.hermes.bootstrap.SipMessageHandler;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;
import reactor.ipc.netty.http.server.HttpServerRequest;
import reactor.ipc.netty.http.server.HttpServerResponse;

import java.util.List;

/**
 * Created by owen_q on 2018. 6. 17..
 */
public class HermesHttpSipHandler extends HermesAbstractSipHandler<HttpServerRequest, HttpServerResponse> {
    private Logger logger = LoggerFactory.getLogger(HermesHttpSipHandler.class);

    HermesHttpSipHandler(List<SipMessageHandler> sipMessageHandlerList, SipMessageConsumer sipMessageConsumer) {
        super(sipMessageHandlerList, sipMessageConsumer);
    }

    @Override
    public Publisher<Void> apply(HttpServerRequest httpServerRequest, HttpServerResponse httpServerResponse) {

        // Add outbound... ?
        FluxProcessor<String, String> server =
                ReplayProcessor.<String>create().serialize();

        FluxProcessor<String, String> client =
                ReplayProcessor.<String>create().serialize();

        server.subscribe();


        // Convert String to DefaultSipMessage

        Mono<Void> result = httpServerResponse.sendWebsocket((i, o) -> {
            return o.sendString(
                    i.receive().asString().subscribeWith(server)
            );
        });

//        return chain(httpServerRequest);
        return result;
    }
}
