package org.owen.proxy.sip.process.stateless;

import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.RecordRoute;
import gov.nist.javax.sip.header.Route;
import gov.nist.javax.sip.header.RouteList;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.message.SIPResponse;
import org.owen.hermes.model.ServerInfo;
import org.owen.hermes.sip.wrapper.message.DefaultSipRequest;
import org.owen.hermes.sip.wrapper.message.proxy.ProxySipRequest;
import org.owen.hermes.sip.wrapper.message.proxy.ProxySipResponse;
import org.owen.proxy.core.Message;
import org.owen.proxy.core.ProxyHandler;
import org.owen.proxy.model.RemoteAddress;
import org.owen.proxy.registrar.Registrar;
import org.owen.proxy.registrar.Registration;
import org.owen.proxy.util.AuthUtil;
import org.owen.proxy.util.ProxyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.SipFactory;
import javax.sip.address.Address;
import javax.sip.address.URI;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Request;

/**
 * Created by dongqlee on 2018. 5. 24..
 */
public class ProxyStatelessRequestHandler implements ProxyHandler {
    private Logger logger= LoggerFactory.getLogger(ProxyStatelessRequestHandler.class);
    private ServerInfo serverInfo=null;
    private Via proxyVia=null;

    private HeaderFactory headerFactory=null;
    private Registrar registrar=null;

    public ProxyStatelessRequestHandler(ServerInfo serverInfo) {
        this.serverInfo=serverInfo;
        this.proxyVia=generateProxyVia(serverInfo);
        this.registrar=Registrar.getInstance();

        try{
            this.headerFactory= SipFactory.getInstance().createHeaderFactory();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * # new-proxy
     * Request-URI 등 필요한 값을 통해서 Client Connection을 찾는다.
     *
     */


    /**
     * # test-proxy
     * * Request:
     * - next hop의 ip, port, transport를 결정 후, Request-URI에 설정한다
     * - Via를 단다.
     */
    /**
     * 1) copy request
     * 2) update request-uri
     * 3) update max-forwards
     * 4) optionally add record-route
     * 5) optionally add additional header
     * 6) postprocess routing information
     * 7) determine next hop ip, port,
     * 8) add via header
     * 9) add content-length
     *
     * -----------------------------------------------------
     *
     * 10) forward new request
     * 11) set timer c
     */

    @Override
    public Message handle(Message message) {
        String method=message.getOriginalMessage().getMethod();

        if(Request.REGISTER.equals(method)){
            handleRegister(message);
        }
        else if(Request.CANCEL.equals(method)){
            // TODO: Handle stateless proxy 'CANCEL' method
        }
        else{
            // 1) Copy request
            copyRequest(message);

            // 2) Update request-uri
            updateRequestURI(message);

            // 3) Update max-forwards
            updateMaxForwards(message);

            // 4) optionally add record-route
//            addRecordRouteHeader(message);

            // 5) optionally add additional header
            //

            // 6)
            postProcessRoutingInformation(message);


            // 아래의 작업들을 공통부분으로 옮긴다.
            // 7) Determine next hop ip, port, transport
            // => Move to @Link{org.owen.hermes.sip.wrapper.message.DefaultSipMessage} send();
            determineNextHop(message);


            // 8)
            /*
                TODO: Move to @Link{org.owen.hermes.sip.wrapper.message.DefaultSipMessage} send();
                External, Internal transport가 다를 수 있으므로, nextHop을 결정함에 따라 해당되는 transport의 Via에 넣는다.
             */
            addViaHeader(message);
        }

        return message;
    }

    private void copyRequest(Message message){
        message.setNewMessage(((ProxySipRequest)message.getOriginalMessage()).clone());
    }

    private void updateRequestURI(Message message){
        // none
    }

    private void updateMaxForwards(Message message){
        ((DefaultSipRequest)message.getNewMessage()).decrementMaxForwards();
    }

    private void addRecordRouteHeader(Message message){
        /*
        <sip:203.240.153.30:5061;transport=tcp;lr;node_host=203.240.153.12;node_port=5080;version=0>
         */
        ProxySipRequest proxySipRequest=(ProxySipRequest) message.getNewMessage();

        RecordRoute recordRoute=new RecordRoute();
        AddressImpl address=new AddressImpl();
        SipUri sipUri=new SipUri();

        try{
            // create sip-uri
            sipUri.setTransportParam("tcp");
            sipUri.setHost(this.serverInfo.getHost());
            sipUri.setPort(this.serverInfo.getPort());
            sipUri.setParameter("lr", null);
            sipUri.setParameter("node_host", this.serverInfo.getHost());
            sipUri.setParameter("node_port", this.serverInfo.getPort()+"");

            address.setAddess(sipUri);

            // create record-route
            recordRoute.setAddress(address);

            proxySipRequest.addHeader(recordRoute);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * RFC3261 section 16.6, item 6
     * @param message
     */
    private void postProcessRoutingInformation(Message message){
        ProxySipRequest proxySipRequest=(ProxySipRequest) message.getNewMessage();
        RouteList routeList=proxySipRequest.getRouteHeaders();

        // Contains Route header
        if(routeList !=null && routeList.size() != 0){
            Route firstRouteHeader=(Route) routeList.getLast();

            if(!firstRouteHeader.hasParameter("lr")){
                try{
                    URI requestUri=proxySipRequest.getRequestURI();
                    Route newRoute=new Route();
                    Address address=new AddressImpl();
                    address.setURI(requestUri);

                    newRoute.setAddress(address);
                    routeList.add(newRoute);

                    // Remove first route
                    routeList.removeFirst();

                    // Set request-uri using route
                    proxySipRequest.setRequestURI(firstRouteHeader.getAddress().getURI());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * RFC3261 section 16.6, item 7
     * @param message
     */
    private void determineNextHop(Message message){
        /*
        Registrar를 뒤져서, Target Connection정보를 찾는다
         */
        ProxySipRequest proxySipRequest=(ProxySipRequest) message.getNewMessage();
        String method=message.getNewMessage().getMethod();
        String targetUserKey="";

        Registration targetRegistration=null;
        /**
         * Method에 따라서, 맞는 Target을 찾는다
         */
        if(Request.INVITE.equals(method)){
            // target=to;
            targetUserKey=ProxyHelper.extractToUserKey(proxySipRequest);
            targetRegistration=registrar.getRegistration(targetUserKey);
        }
        else if(Request.ACK.equals(method)){
            // target=to;
            targetUserKey=ProxyHelper.extractToUserKey(proxySipRequest);
            targetRegistration=registrar.getRegistration(targetUserKey);
        }
        else if(Request.BYE.equals(method)){
            // target=to;
            targetUserKey=ProxyHelper.extractToUserKey(proxySipRequest);
            targetRegistration=registrar.getRegistration(targetUserKey);
        }
        else if(Request.CANCEL.equals(method)){
            // TODO:
        }

        // set request uri
        SipUri targetRequestURI=new SipUri();

        try{
            targetRequestURI.setHost(targetRegistration.getRemoteAddress().getHost());
            targetRequestURI.setPort(targetRegistration.getRemoteAddress().getPort());
            targetRequestURI.setTransportParam(targetRegistration.getRemoteAddress().getTransport().getValue());

            proxySipRequest.setRequestURI(targetRequestURI);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @param message
     */
    private void addViaHeader(Message message){
        if(logger.isDebugEnabled())
            logger.debug("Add via header");

        Via newProxyVia=(Via) this.proxyVia.clone();

        try{
            newProxyVia.setBranch(ProxyHelper.generateBranch(message.getNewMessage()));
            ((ProxySipRequest)message.getNewMessage()).addVia(this.proxyVia);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private Via generateProxyVia(ServerInfo serverInfo){
        Via via=new Via();
        try{
            via.setHost(serverInfo.getHost());
            via.setPort(serverInfo.getPort());
            via.setReceived(serverInfo.getHost());
            via.setTransport(serverInfo.getTransport().getValue());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return via;
    }

    private void addContentLength(){

    }

    private void forwardNewRequest(){

    }

    // TODO: implement on next step, Stateful
    private void setTimerC(){

    }

    public Message handleRegister(Message message) {
        ProxySipRequest registerRequest=(ProxySipRequest) message.getOriginalMessage();
        Header authHeader= registerRequest.getHeader("Authorization");
        String authorization="";
        ProxySipResponse registerResponse=null;

        if(authHeader==null){
            // non-auth
            registerResponse=((ProxySipRequest) registerRequest).createResponse(SIPResponse.UNAUTHORIZED, SIPResponse.getReasonPhrase(SIPResponse.UNAUTHORIZED));
//            String domain=registerRequest.getFrom().getAddress().getURI().toString().split("@")[1];

            String domainName		= registerRequest.getFrom().getAddress().getURI().toString().split("@")[1];
            if (domainName.contains(":")) {
                domainName = domainName.split(":")[0];
            }

            try{
                WWWAuthenticateHeader wwwAuthenticateHeader=this.headerFactory.createWWWAuthenticateHeader("Digest");
                wwwAuthenticateHeader.setAlgorithm("MD5");
                wwwAuthenticateHeader.setQop("auth");
                wwwAuthenticateHeader.setNonce(AuthUtil.getNonce());
                wwwAuthenticateHeader.setRealm(domainName);

                Header tmpHeader=this.headerFactory.createHeader("WWW-Authenticate", AuthUtil.getAuthorization(domainName));

                registerResponse.addHeader(tmpHeader);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            // do auth
            String aor="";
            String account="";
            String domain="";
            String userKey="";
//            String ipPhonePassword="aaaaaa";
            String ipPhonePassword="4372d100b37c6375426df97ff42375e6aeb4b1a8162d4bf6fc6522815d884a60";
            //4372d100b37c6375426df97ff42375e6aeb4b1a8162d4bf6fc6522815d884a60

            aor=registerRequest.getFrom().getAddress().getURI().toString().split(":")[1];
            account=aor.split("@")[0];
            domain=aor.split("@")[1];


            userKey=aor;// 1단계에서는 OPMD 지원 고려 안하는걸로.

            authorization=authHeader.toString();

            // TODO(owen): get password from rest
            /*
            HttpService httpService=HttpService.getInstance();

            try{
                JsonObject response=httpService.get("/ims/users/"+aor+"/password", JsonObject.class);
                String status=response.getAsJsonObject("header").get("status").getAsString();
                if (!status.equals("error")) {
                    ipPhonePassword = response.get("body").getAsJsonObject().get("telNoPassword").getAsString();
                }
            }
            catch (Exception e){
                e.printStackTrace();
                // return
            }
            */

            AuthUtil authUtil=new AuthUtil(authorization);
            authUtil.setPassword(ipPhonePassword);
            authUtil.isValid();

            if(authUtil.isEqualHA()){
                // Auth success
                registerResponse=((ProxySipRequest) registerRequest).createResponse(SIPResponse.OK);

                //TODO: get first via received & rport
                RemoteAddress clientRemoteAddress=ProxyHelper.getClientRemoteAddress(registerRequest);

                Registration registration=new Registration(userKey, aor,account, domain, clientRemoteAddress);

                registrar.register(userKey, registration);
//                jedisConnection.set(userKey, gson.toJson(registration));
            }
            else{
                logger.warn("REGISTER Fail");
            }
        }

        message.setNewMessage(registerResponse);

        return message;
    }

}
