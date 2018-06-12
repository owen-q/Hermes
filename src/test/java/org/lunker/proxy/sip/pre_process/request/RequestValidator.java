package org.lunker.proxy.sip.pre_process.request;

import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipRequest;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipResponse;
import org.lunker.proxy.core.Message;
import org.lunker.proxy.core.ProxyHandler;
import org.lunker.proxy.util.ProxyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.header.Header;
import javax.sip.header.MaxForwardsHeader;

/**
 * Created by dongqlee on 2018. 5. 21..
 */
public class RequestValidator implements ProxyHandler{
    private Logger logger= LoggerFactory.getLogger(RequestValidator.class);

    /**
     * Validate sip request according to rfc 3261 section 16.3
     */
    @Override
    public Message handle(Message message) {
//        message.invalidate(SIPResponse.OK, "", null);

        checkSipUriScheme(message)
                .checkMaxForwards(message)
                .checkRequestLoop(message);

        return message;
    }

    private RequestValidator checkSipUriScheme(Message message){
        if (!((DefaultSipRequest) message.getOriginalMessage()).getRequestURI().isSipURI()) {
            try {
                // Publish 416 Unsupported_URI_Scheme
                DefaultSipResponse unsupportedUriSchemeResponse = ((DefaultSipRequest) message.getOriginalMessage()).createResponse(SIPResponse.UNSUPPORTED_URI_SCHEME);

                message.invalidate(SIPResponse.UNSUPPORTED_URI_SCHEME, "", unsupportedUriSchemeResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return this;
    }

    /**
     * Check Max-Forwards Header
     * @param message
     * @return
     */
    private RequestValidator checkMaxForwards(Message message){
        MaxForwardsHeader maxForwardsHeader=message.getOriginalMessage().getMaxForwards();

        // pass check
        if(maxForwardsHeader==null){
            return this;
        }
        else{
            int maxForwards=0;
            maxForwards=maxForwardsHeader.getMaxForwards();

            if(maxForwards == 0){
                if(SIPRequest.INFO.equals(message.getOriginalMessage().getMethod())){

//                    message.getValidation().setValidate(false);

                    // TOOD: 올바른 response발행
                }
                else{
                    // ?
                }
            }
            else if (maxForwards >0){
                //pass check
                return this;
            }
            else if(maxForwards < 0){
                // TODO: create 483 response
                try{
                    DefaultSipResponse tooManyHopsResponse=((DefaultSipRequest)message.getOriginalMessage()).createResponse(SIPResponse.TOO_MANY_HOPS);
                    message.invalidate(SIPResponse.TOO_MANY_HOPS, "", tooManyHopsResponse);
                }
                catch (Exception e){
                    e.printStackTrace();
                    // TODO: create 500 ServerInternal Error Response common
                }
            }
        }

        return this;
    }

    /**
     * Check whether received sip request is looped
     * @param message
     * @return
     */
    private RequestValidator checkRequestLoop(Message message){
        String branch=message.getOriginalMessage().getTopmostVia().getBranch();
        String expectedBranch="";

        // generate branch value
        expectedBranch= ProxyHelper.generateBranch(message.getOriginalMessage());

        if(branch.equals(expectedBranch)){
            // Loop detect
            try{
                DefaultSipResponse loopDetectedResponse=((DefaultSipRequest)message.getOriginalMessage()).createResponse(SIPResponse.LOOP_DETECTED);
                message.invalidate(SIPResponse.LOOP_DETECTED, "", loopDetectedResponse);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        return this;
    }

    // TODO
    private RequestValidator checkProxyRequire(Message message){
        Header proxyRequireHeader=message.getOriginalMessage().getHeader("Proxy-Require");

        if(proxyRequireHeader==null){

        }
        else{

        }

        return this;
    }

    // TODO
    private RequestValidator checkProxyAuthorization(Message message){
        Header proxyAuthorizationHeader=message.getOriginalMessage().getHeader("Proxy-Authorization");

        if(proxyAuthorizationHeader==null){

            return this;
        }
        else{
            // TODO: check auth
        }

        return this;
    }
}
