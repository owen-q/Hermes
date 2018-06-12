package org.lunker.proxy.sip.pre_process;

import org.lunker.new_proxy.model.ServerInfo;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipRequest;
import org.lunker.proxy.core.Message;
import org.lunker.proxy.core.ProcessState;
import org.lunker.proxy.core.ProxyHandler;
import org.lunker.proxy.sip.pre_process.request.RequestTargetDetector;
import org.lunker.proxy.sip.pre_process.request.RequestValidator;
import org.lunker.proxy.sip.pre_process.request.RoutePreprocessor;
import org.lunker.proxy.sip.pre_process.response.ViaRemover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Created by dongqlee on 2018. 5. 1..
 */
public class ProxyPreHandler implements ProxyHandler {
    private Logger logger= LoggerFactory.getLogger(ProxyPreHandler.class);

    // Request pre-handler
    private RequestValidator requestValidator=null;
    private RoutePreprocessor routePreprocessor=null;
    private RequestTargetDetector requestTargetDetector=null;

    // Response pre-handler
    private ProxyHandler viaRemover=null;

    private Validator validator=null;

    public ProxyPreHandler() {
    }

    public ProxyPreHandler(ServerInfo serverInfo) {
        // pre-handler
        this.requestValidator=new RequestValidator();
        this.routePreprocessor=new RoutePreprocessor();
        this.requestTargetDetector=new RequestTargetDetector();

        this.viaRemover=new ViaRemover(serverInfo);

        this.validator=new Validator();
    }

    @Override
    public Message handle(Message message) {
        if(message.getProcessState() != ProcessState.PRE) {
            //TODO: State check 공통화
            return message;
        }

        if(logger.isDebugEnabled())
            logger.debug("In ProxyPreHandler");

        Mono<Message> preProcessMono=null;

        if(message.getOriginalMessage() instanceof DefaultSipRequest){
            //Request Preprocessing
            preProcessMono=Mono.just(message)
                    .map(requestValidator::handle)
                    .filter(validator)
                    .map(routePreprocessor::handle)
                    .filter(validator)
                    .map(requestTargetDetector::handle);
        }
        else{
            //Response Preprocessing
            preProcessMono=Mono.just(message).map(viaRemover::handle);
        }

        preProcessMono.subscribeOn(Schedulers.single());
        preProcessMono.block();

        // Change message state
        if(message.getValidation().isValidate())
            message.setProcessState(ProcessState.IN);
        else
            message.setProcessState(ProcessState.POST);

        return message;
    }
}
