package org.owen.hermes.sip.wrapper.message;

import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;

import javax.sip.address.URI;
import javax.sip.header.ContentTypeHeader;
import javax.sip.message.Response;
import java.text.ParseException;

/**
 * Created by dongqlee on 2018. 4. 28..
 */
public class DefaultSipRequest extends DefaultSipMessage {
    private SIPRequest sipReqeust =null;

    public DefaultSipRequest(SIPMessage sipMessage) {
        super(sipMessage);

        this.sipReqeust =(SIPRequest) this.message;
    }

    public void setContent(Object content, String contentType) {
        try{
            ContentTypeHeader contentTypeHeader=null;

            contentTypeHeader=(ContentTypeHeader)this.message.getHeader("Content-Type");
            contentTypeHeader.setContentType("application");
            contentTypeHeader.setContentSubType("sdp");

            this.message.setContent(content, contentTypeHeader);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void checkContentType(String contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("the content type cannot be null");
        } else {
            int indexOfSlash = contentType.indexOf("/");
            if (indexOfSlash != -1) {
                /*
                if (!JainSipUtils.IANA_ALLOWED_CONTENT_TYPES.contains(contentType.substring(0, indexOfSlash))) {
                    throw new IllegalArgumentException("the given content type " + contentType + " is not allowed");
                }
                */
                throw new IllegalArgumentException("the given content type " + contentType + " is not allowed");
            } else {
                throw new IllegalArgumentException("the given content type " + contentType + " is not allowed");
            }

        }
    }

    public String getCharacterEncoding() {
        if (this.message.getContentEncoding() != null) {
            return this.message.getContentEncoding().getEncoding();
        } else {
            ContentTypeHeader cth = (ContentTypeHeader)this.message.getHeader("Content-Type");
            return cth == null ? null : cth.getParameter("charset");
        }
    }

    public Object getContent(){
        return this.message.getContent();
    }

    public URI getRequestURI(){
        SIPRequest sipRequest=(SIPRequest) this.message;
        return sipRequest.getRequestURI();
    }

    public void setRequestURI(URI requestURI){
        SIPRequest sipRequest=(SIPRequest) this.message;
        sipRequest.setRequestURI(requestURI);
    }

    public void addVia(Via via){
        this.sipReqeust.getViaHeaders().addFirst(via);
    }

    public void removeTopVia(){
        
    }

    public void decrementMaxForwards(){
        try{
            this.message.getMaxForwards().decrementMaxForwards();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public DefaultSipResponse createResponse(int responseCode) throws ParseException{
        DefaultSipResponse defaultSipResponse=null;
        Response response=null;

        response=this.sipMessageFactory.createResponse(responseCode, this.sipReqeust);
        defaultSipResponse=new DefaultSipResponse((SIPResponse) response);

        return defaultSipResponse;
    }

    @Override
    public Object clone() {
        SIPRequest sipRequest=(SIPRequest) this.sipReqeust.clone();
        return new DefaultSipRequest(sipRequest);
    }
}
