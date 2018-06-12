package org.lunker.new_proxy.sip.session.ss;

import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.message.SIPRequest;
import io.netty.channel.ChannelHandlerContext;
import org.lunker.new_proxy.core.ProxyContext;
import org.lunker.new_proxy.sip.session.sas.SipApplicationSessionKey;
import org.lunker.new_proxy.sip.util.SipMessageFactory;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipRequest;
import org.lunker.new_proxy.stub.session.sas.SipApplicationSession;
import org.lunker.new_proxy.stub.session.ss.SipSession;

import javax.sip.address.Address;
import javax.sip.header.*;
import javax.sip.message.Request;
import java.util.*;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class SipSessionImpl implements SipSession {

    private SipSessionKey sipSessionKey;
    private SipApplicationSessionKey sipApplicationSessionKey;

    private ProxyContext proxyContext=null;
    private SipMessageFactory sipMessageFactory=null;
    private ChannelHandlerContext ctx;
    private Map<String, Object> sessionAttributes;
    private ProxySipRequest firstRequest=null;

    private SipSessionImpl() {
        this.proxyContext=ProxyContext.getInstance();
        this.sipMessageFactory=SipMessageFactory.getInstance();
    }

    public SipSessionImpl(SipSessionKey sipSessionKey, SipApplicationSessionKey sipApplicationSessionKey) {
        this.sipSessionKey = sipSessionKey;
        this.sipApplicationSessionKey=sipApplicationSessionKey;
        this.sessionAttributes=new HashMap<>();
        this.sipMessageFactory=SipMessageFactory.getInstance();
        this.proxyContext=ProxyContext.getInstance();
    }

    @Override
    public SipApplicationSession getSipApplicationSession() {
        return this.proxyContext.getSipApplicationSession(sipApplicationSessionKey);
    }

    @Override
    public SipSessionKey getSipSessionkey() {
        return this.sipSessionKey;
    }

    @Override
    public void setAttribute(String key, Object value) {
        this.sessionAttributes.put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return this.sessionAttributes.get(key);
    }

    @Override
    public ProxySipRequest createRequest(String method) {

        /**
         * 현재 SipSession에 존재하는 Sip Request를 이용하여 새로운 SipRequest를 만든다.
         * 그리고 새롭게 생성된 SipRequest를 위한 SipSession을 생성한다
         */

        /**
         * - SAS 동일
         * - 다른 CallId
         * - 새로운 FromTag
         * - Full Max-Forwards
         * - 새로운 Via. . .**** (Via: SIP/2.0/TCP 203.240.153.13:5080;branch=z9hG4bK5a8dcb6a_e57a7a8c_d3e18d01-5f94-40ef-91ec-8468232a34a3)
         * - Record-Route-> Route로 사용
         * - 기타 Header복사
         * - Contact -> 뭐냐 이건 ?
         * - User-Agent:
         */

        /**
         * - Call-ID
         * - CSeq:
         * - Via:
         * - Original-call-id:
         * - Contact:
         * - Max-Forward:
         * - User-Agent:
         * - Route
         * - Content-Type
         */

        if(!"INVITE".equals(method) && !"REGISTER".equals(method)){
            return null;
        }

        ProxySipRequest createdRequest=null;
        SipSession newSipSession=null;

        try {
            Request originalRequest=null;
            SIPRequest newRequest=null;
            FromHeader originalFromHeader=null;
            ToHeader originalToHeader=null;
            ContactHeader originalContactHeader=null;

            newRequest=new SIPRequest();
            newRequest.setMethod(method);

            originalRequest = (Request) this.firstRequest.getRawSipMessage();

            originalFromHeader=(FromHeader) originalRequest.getHeader("From");
            originalToHeader=(ToHeader) originalRequest.getHeader("To");

            // new-feature: Create Call-ID:
            // random string@currentIp
            String newCallId="";
            newCallId=this.sipMessageFactory.generateCallId();
            CallIdHeader newCallIdHeader=this.sipMessageFactory.getHeaderFactory().createCallIdHeader(newCallId);

            // new-feature: Create From:
            // generate new tag
            // (hashed) random string_hashed callid_sasid
            String fromTag="";
            fromTag=this.sipMessageFactory.generateTag(newCallId, this.sipSessionKey.getApplicationSessionId());

            FromHeader newFromHeader=null;
            newFromHeader=(FromHeader) originalFromHeader.clone();
            newFromHeader.setTag(fromTag);

            // new-feature: Create To:
            ToHeader newToHeader=null;
            newToHeader=(ToHeader) originalToHeader.clone();

            // new-feature: Create Max-Forwards:
            MaxForwardsHeader newMaxForwardHeader=null;
            newMaxForwardHeader=this.sipMessageFactory.getHeaderFactory().createMaxForwardsHeader(70);

            // new-feature: Create Contact:
            ContactHeader newContactHeader=null;

            originalContactHeader=(ContactHeader) originalRequest.getHeader("Contact");
            newContactHeader=this.sipMessageFactory.getHeaderFactory().createContactHeader();
            // Contact: <sip:07079159144@203.240.153.11:5072;transport=tcp>^M
            // originalRequest의 from's displayName@connectd LB address; transport with LB
            // newContactHeader.

            Iterator<String> originalContactParameterItr=originalContactHeader.getParameterNames();
            while(originalContactParameterItr.hasNext()){
                String name=originalContactParameterItr.next();
                newContactHeader.setParameter(name, originalContactHeader.getParameter(name));
            }

            SipUri newUri=new SipUri();
            newUri.setHost("10.0.8.2");
            newUri.setPort(10010);

            Address newAddress=this.sipMessageFactory.getAddressFactory().createAddress(originalFromHeader.getAddress().getURI().toString().split(":")[1], newUri);
            newContactHeader.setAddress(newAddress);
            newContactHeader.removeParameter("expires");

            // new-feature: Create Via:
            ViaHeader newViaHeader=this.sipMessageFactory.getHeaderFactory().createViaHeader("10.0.8.2", 10010, "tcp", this.sipMessageFactory.generateBranch());

            // new-feature: Create Route: (Optional)
            if(originalRequest.getHeader("Record-Route") != null){
                Iterator<RecordRouteHeader> recordRouteHeaderItr=originalRequest.getHeaders("Record-Route");

                RouteHeader routeHeader=null;
                RecordRouteHeader recordRouteHeader=null;

                while(recordRouteHeaderItr.hasNext()){
                    recordRouteHeader=recordRouteHeaderItr.next();
                    routeHeader=this.sipMessageFactory.getHeaderFactory().createRouteHeader(recordRouteHeader.getAddress());
                    newRequest.addHeader(routeHeader);
                }
            }

            // new-feature: Create User-Agent:
            List<String> newUserAgentList=new ArrayList<>();
            newUserAgentList.add("New Proxy");

            UserAgentHeader newUserAgentHeader=this.sipMessageFactory.getHeaderFactory().createUserAgentHeader(newUserAgentList);

            // new-feature: Create Content-Type:
            ContentTypeHeader newContentTypeHeader=this.sipMessageFactory.getHeaderFactory().createContentTypeHeader("application", "sdp");

            // new-feature: Create CSeq:
            CSeqHeader originalCSeqHeader=(CSeqHeader) this.firstRequest.getHeader("CSeq");
            CSeqHeader newCSeqHeader=null;

            if(originalCSeqHeader.getMethod().equals(method)){
                newCSeqHeader=this.sipMessageFactory.getHeaderFactory().createCSeqHeader(originalCSeqHeader.getSeqNumber()+1, method);
            }
            else{
                newCSeqHeader=this.sipMessageFactory.getHeaderFactory().createCSeqHeader(1, method);
            }

            newRequest.addHeader(newCallIdHeader);
            newRequest.addHeader(newFromHeader);
            newRequest.addHeader(newToHeader);
            newRequest.addHeader(newMaxForwardHeader);
            newRequest.addHeader(newContactHeader);
            newRequest.addHeader(newViaHeader);
            newRequest.addHeader(newUserAgentHeader);
            newRequest.addHeader(newContentTypeHeader);
            newRequest.addHeader(newCSeqHeader);

            // Create Content-Length:
            SipSessionKey newSipSessionKey=new SipSessionKey(newRequest, this.sipSessionKey.getApplicationSessionId());
//            createdRequest=new ProxySipRequest(newRequest, newSipSessionKey);

            ChannelHandlerContext targetCtx=findTarget(newToHeader.getAddress().getURI().toString().split(":")[1]);
//            newSipSession=proxyContext.createOrGetSIPSession(targetCtx, createdRequest); // 쌩뚱맞은 sasId를 가져온다 ㅡㅡ

            System.out.println("debug breakpoint");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if("INVITE".equals(method)){
            newSipSession.setFirstRequest(createdRequest);
        }

        return createdRequest;
    }

    // find To user-agent registration info

    public ChannelHandlerContext findTarget(String aor){
//        return this.proxyContext.getRegistrar().getCtx(aor);
        return null;
    }

    @Override
    public void setFirstRequest(ProxySipRequest generalSipRequest) {
        this.firstRequest=generalSipRequest;
    }

    @Override
    public ProxySipRequest getFirstRequest() {
        return this.firstRequest;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}

