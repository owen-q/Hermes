package org.owen.hermes.bootstrap;

import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.header.ViaList;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.parser.StringMsgParser;
import org.owen.hermes.core.HermesMessage;
import org.owen.hermes.sip.wrapper.message.DefaultSipMessage;
import org.owen.hermes.sip.wrapper.message.DefaultSipRequest;
import org.owen.hermes.sip.wrapper.message.DefaultSipResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.text.ParseException;

/**
 * Created by owen_q on 2018. 6. 18..
 */
public class HermesMessageConverter {
    private Logger logger = LoggerFactory.getLogger(HermesMessageConverter.class);

    private StringMsgParser stringMsgParser = null;

    // TODO: decide sharable or instance
    public HermesMessageConverter() {
        this.stringMsgParser = new StringMsgParser();
    }

    public static HermesMessageConverter getInstance(){
        return Holder.INSTANCE;
    }

    // TODO
    public HermesMessage convertStringToHermesMessage(String sipRawMessage){
        return null;
    }

    public DefaultSipMessage convertStringToDefaultSipMessage(InetSocketAddress inetSocketAddress, String rawSipMessage) {
        SIPMessage sipMessage = null;
        DefaultSipMessage defaultSipMessage = null;

        try{
            sipMessage = generateJainSipMessage(rawSipMessage);
            sipMessage = updateMessage(inetSocketAddress, sipMessage);
            defaultSipMessage = generateGeneralSipMessage(sipMessage);
        }
        catch (ParseException pe){
            defaultSipMessage = DefaultSipMessage.empty();
        }

        return defaultSipMessage;
    }

    public DefaultSipMessage convertStringToDefaultSipMessage(String rawSipMessage) {
        SIPMessage sipMessage = generateJainSipMessage(rawSipMessage);
        return generateGeneralSipMessage(sipMessage);
    }

    private SIPMessage generateJainSipMessage(String strSipMessage)  {
        SIPMessage sipMessage = null;

        try{
            sipMessage = stringMsgParser.parseSIPMessage(strSipMessage.getBytes(), true, false, null);
        }
        catch (ParseException pe){
            ;
        }
        return sipMessage;
    }

    /**
     * Set ServerReflexive address to Via 'rport', 'received'
     * @param inetSocketAddress
     * @param jainSipMessage
     * @return
     * @throws ParseException
     */
    private SIPMessage updateMessage(InetSocketAddress inetSocketAddress, SIPMessage jainSipMessage) throws ParseException {

        if(jainSipMessage instanceof SIPRequest){
            ViaList viaList = jainSipMessage.getViaHeaders();

            Via topViaHeader=(Via) viaList.getFirst();

            if (topViaHeader.getReceived() == null) {
                String received = inetSocketAddress.getHostString();
                topViaHeader.setReceived(received);
            }

            if(topViaHeader.getRPort() == 0 || topViaHeader.getRPort() == -1) {
                int rport = inetSocketAddress.getPort();

                topViaHeader.setParameter("rport", rport+"");
            }

            viaList.set(0, topViaHeader);
            jainSipMessage.setHeader(viaList);
        }

        return jainSipMessage;
    }

    private DefaultSipMessage generateGeneralSipMessage(SIPMessage jainSipMessage){
        DefaultSipMessage defaultSipMessage = null;

        if(jainSipMessage == null)
            return DefaultSipMessage.empty();

        if(jainSipMessage instanceof SIPRequest){
            defaultSipMessage = new DefaultSipRequest(jainSipMessage);
        }
        else{ // jainSipMessage instanceof SIPResponse
            defaultSipMessage = new DefaultSipResponse(jainSipMessage);
        }

        return defaultSipMessage;
    }

    private static class Holder {
        private static HermesMessageConverter INSTANCE = new HermesMessageConverter();
    }
}
