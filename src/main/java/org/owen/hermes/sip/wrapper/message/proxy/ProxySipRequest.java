package org.owen.hermes.sip.wrapper.message.proxy;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import org.owen.hermes.model.ServerInfo;
import org.owen.hermes.sip.wrapper.message.DefaultSipRequest;

import javax.sip.address.Address;
import javax.sip.header.ContactHeader;
import javax.sip.header.RecordRouteHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.ListIterator;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class ProxySipRequest extends DefaultSipRequest {
    private ServerInfo serverInfo=null;

    public ProxySipRequest(SIPMessage jainSipRequest) {
        super(jainSipRequest);
    }

    public ProxySipResponse createResponse(int statusCode) {
        return this.createResponse(statusCode,SIPResponse.getReasonPhrase(statusCode));
    }

    public ProxySipResponse createResponse(int statusCode, String reasonPhrase) {
        try {
            Request request = (Request)this.message;
            Response response = this.sipMessageFactory.createResponse(statusCode, request);
            if (reasonPhrase != null) {
                response.setReasonPhrase(reasonPhrase);
            }

            String requestMethod = this.getMethod();
            if (statusCode > 100  && statusCode <= 606) {

                // set to tag
                ToHeader toHeader = (ToHeader)response.getHeader("To");

                // Set toTag
                if(toHeader.getTag() == null){

                    //  TODO(owen): using SipSession
//                    String applicatoinSessionId=this.sipSessionKey.getApplicationSessionId();
//                    toHeader.setTag(applicatoinSessionId);

                    // Generate ToTag
                    this.sipMessageFactory.generateTag(getCallId(), "");
                }
                // end set to-tag

                // set contact header
                boolean setContactHeader = true;
                if (statusCode >= 300 && statusCode < 400 || statusCode == 485 || "REGISTER".equals(requestMethod) || "OPTIONS".equals(requestMethod) || "BYE".equals(requestMethod) || "CANCEL".equals(requestMethod) || "PRACK".equals(requestMethod) || "MESSAGE".equals(requestMethod) || "PUBLISH".equals(requestMethod)) {
                    setContactHeader = false;
                }

                if (setContactHeader) {
                    /* // TODO:
                    ContactHeader contactHeader = JainSipUtils.createContactHeader(super.sipFactoryImpl.getSipNetworkInterfaceManager(), request, (String)null, (String)null, outboundInterface);
                    String transport = "tcp";
                    if (session != null && session.getTransport() != null) {
                        transport = session.getTransport();
                    }
                    */


                    // Proxy ServerInfo..
                    Address address=this.sipMessageFactory.getAddressFactory().createAddress("10.0.1.202:10010");
                    ContactHeader contactHeader=this.sipMessageFactory.getHeaderFactory().createContactHeader(address);
                    contactHeader.setParameter("transport", "tcp");

                    response.setHeader(contactHeader);
                }
            }

            // Set Record-Route
            if ("INVITE".equals(requestMethod)) {
                ListIterator recordRouteHeaders = request.getHeaders("Record-Route");

                while(recordRouteHeaders.hasNext()) {
                    RecordRouteHeader recordRouteHeader = (RecordRouteHeader)recordRouteHeaders.next();
                    response.addHeader(recordRouteHeader);
                }
            }

//            ProxySipResponse generalSipResponse=new ProxySipResponse((SIPResponse) response, this.sipSessionKey);
            ProxySipResponse generalSipResponse=new ProxySipResponse((SIPResponse) response);
            return generalSipResponse;
        } catch (ParseException var19) {
            throw new IllegalArgumentException("Bad status code " + statusCode, var19);
        }
    }// end method

    @Override
    public ProxySipRequest clone() {
        SIPRequest sipRequest=(SIPRequest) this.message.clone();
        return new ProxySipRequest(sipRequest);
    }
}
