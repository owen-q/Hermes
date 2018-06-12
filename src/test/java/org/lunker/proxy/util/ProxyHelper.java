package org.lunker.proxy.util;

import gov.nist.javax.sip.header.Via;
import org.lunker.new_proxy.model.Transport;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipMessage;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipRequest;
import org.lunker.proxy.model.RemoteAddress;
import org.lunker.proxy.registrar.Registrar;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class ProxyHelper {
    private static Registrar registrar=null;
    private static int MIN_CALLID_LENGTH=20;
    private static int MAX_CALLID_LENGTH=50;
    private static int MAX_TAG_LENGTH=30;

    private static int OPPORTUNITY=40;
    private static Random random=null;

    private ProxyHelper() {
    }

    public ProxyHelper getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public static String generateBranch(DefaultSipMessage defaultSipMessage){
        String branch="";
        StringBuilder branchBuilder=new StringBuilder();
        String fromTag="";
        String toTag="";
        String callId="";
        String requestUrl="";
        String topMostVia="";
        long cSeqNum=0;

        fromTag=defaultSipMessage.getFrom().getTag();
        toTag=defaultSipMessage.getTo().getTag();
        callId=defaultSipMessage.getCallId();

        if(defaultSipMessage instanceof DefaultSipRequest)
            requestUrl=((DefaultSipRequest) defaultSipMessage).getRequestURI().toString();

        topMostVia=defaultSipMessage.getTopmostVia().toString();
        cSeqNum=defaultSipMessage.getCSeq().getSeqNumber();


        branchBuilder.append(fromTag).append(toTag).append(callId).append(requestUrl).append(topMostVia).append(cSeqNum);

        try{
            branch=md5Hash(branchBuilder.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return branch;
    }

    private static String md5Hash(String value) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(value.getBytes());
        byte[] digest = md.digest();

        String myHash = DatatypeConverter
                .printHexBinary(digest).toUpperCase();
        return myHash;
    }

    public static RemoteAddress getClientRemoteAddress(DefaultSipMessage defaultSipMessage){
        Via firstVia=(Via) defaultSipMessage.getViaHeaders().getFirst();

        return new RemoteAddress(Transport.valueOf(firstVia.getTransport().toUpperCase()), firstVia.getReceived(), firstVia.getRPort());
    }

    public static String extractFromUserKey(DefaultSipRequest defaultSipRequest){
        return defaultSipRequest.getFrom().getAddress().getURI().toString().split(":")[1];
    }

    public static String extractToUserKey(DefaultSipRequest defaultSipRequest){
        return defaultSipRequest.getTo().getAddress().getURI().toString().split(":")[1];
    }

    private static class SingletonHolder{
        private static ProxyHelper INSTANCE=new ProxyHelper();
    }
}
