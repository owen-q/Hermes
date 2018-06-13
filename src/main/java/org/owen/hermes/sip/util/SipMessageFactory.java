package org.owen.hermes.sip.util;

import gov.nist.javax.sip.parser.StringMsgParser;
import org.owen.hermes.model.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.SipFactory;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.HeaderFactory;
import javax.sip.header.RecordRouteHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.text.ParseException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by dongqlee on 2018. 3. 27..
 */
public class SipMessageFactory {
    private Logger logger= LoggerFactory.getLogger(SipMessageFactory.class);

    private SipFactory sipFactory=null;
    private HeaderFactory headerFactory=null;
    private StringMsgParser stringMsgParser=null;
    private MessageFactory messageFactory=null;
    private AddressFactory addressFactory=null;
    private Random random=null;

    private static int MIN_CALLID_LENGTH=20;
    private static int MAX_CALLID_LENGTH=50;
    private static int MAX_TAG_LENGTH=20;
    private static int MAX_BRANCH_LENGTH=60;

    private static int OPPORTUNITY=40;

    private String IP="10.0.1.202";
    private String hashKey="hermes";

    private SipMessageFactory() {
        this.stringMsgParser=new StringMsgParser();

        try{
            this.sipFactory=javax.sip.SipFactory.getInstance();
            this.headerFactory=sipFactory.createHeaderFactory();
            this.messageFactory=sipFactory.createMessageFactory();
            this.addressFactory=sipFactory.createAddressFactory();
            this.random=new Random();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static SipMessageFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    /**
     * Create Jain DefaultSipResponse
     * @param responseCode
     * @param request
     * @return
     * @throws ParseException
     */
    public Response createResponse(int responseCode, Request request) throws ParseException{
        return this.messageFactory.createResponse(responseCode, request);
    }

    public HeaderFactory getHeaderFactory() {
        return headerFactory;
    }

    public AddressFactory getAddressFactory() {
        return addressFactory;
    }

    public String generateCallId(){
        // randStr_hashed callid@ip

        String randomStr=generateRandStr(MIN_CALLID_LENGTH);

        return randomStr+"@"+IP;
    }

    public String generateTag(String callId, String sipApplicaionSessionId){
        StringBuilder stringBuilder=new StringBuilder();

        stringBuilder.append(generateRandStr(MAX_TAG_LENGTH));
        stringBuilder.append("_");
        stringBuilder.append(hash(callId));
        stringBuilder.append("_");
        stringBuilder.append(sipApplicaionSessionId);

        return stringBuilder.toString();
    }

    public String generateBranch(){
        return generateRandStr(MAX_BRANCH_LENGTH);
    }

    public String generateRandStr(int maxLength) {
        StringBuilder stringBuilder=new StringBuilder();
        int chance=0;

        for (int idx=0; idx<maxLength; idx++){
            chance=this.random.nextInt(OPPORTUNITY);

            if(chance<10)
                stringBuilder.append((char)(ThreadLocalRandom.current().nextInt(0, 25) + 'a'));
            else if (chance<20)
                stringBuilder.append((char)(ThreadLocalRandom.current().nextInt(0, 25) + 'A'));
            else if(chance<35)
                stringBuilder.append(ThreadLocalRandom.current().nextInt(0, 9));
            else
                stringBuilder.append('_');
        }
        return stringBuilder.toString();
    }

    public RecordRouteHeader generateRecordRouteHeader(String user, String host, int port, String protocol) throws ParseException {
        SipURI uri = addressFactory.createSipURI(user, host);
        uri.setPort(port);
        uri.setTransportParam(protocol);
        uri.setLrParam(); // RFC 3261 19.1.1
        Address address = addressFactory.createAddress(uri);
        address.setURI(uri);
        return headerFactory.createRecordRouteHeader(address);
    }

    private String hash(String value){
        String hashed=value;

        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(hashKey.getBytes());
            byte[] digest = md.digest();
            hashed= DatatypeConverter
                    .printHexBinary(digest).toUpperCase();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return hashed;
    }

    private static class SingletonHolder{
        private static SipMessageFactory INSTANCE=new SipMessageFactory();
    }
}
