package org.owen.hermes.bootstrap;

import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.header.ViaList;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.parser.StringMsgParser;
import org.owen.hermes.sip.wrapper.message.DefaultSipMessage;
import org.owen.hermes.sip.wrapper.message.DefaultSipRequest;
import org.owen.hermes.sip.wrapper.message.DefaultSipResponse;
import org.owen.hermes.util.lambda.StreamHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.ipc.netty.NettyInbound;

import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.Optional;

/**
 * Created by owen_q on 2018. 6. 18..
 */
public class HermesMessageConverter {
    private Logger logger = LoggerFactory.getLogger(HermesMessageConverter.class);

    private StringMsgParser stringMsgParser = null;

    private HermesMessageConverter() {
        this.stringMsgParser = new StringMsgParser();
    }

    public static HermesMessageConverter getInstance(){
        return Holder.INSTANCE;
    }

    public Optional<DefaultSipMessage> deserialize(NettyInbound nettyInbound, String rawSipMessage) {
        return Optional.ofNullable(rawSipMessage)
                .map(StreamHelper.wrapper(strSipMessage -> generateJainSipMessage(rawSipMessage)))
                .map(StreamHelper.wrapper(jainSipMessage -> updateMessage(nettyInbound, jainSipMessage)))
                .map(jainSipMessage -> generateGeneralSipMessage(nettyInbound, jainSipMessage));
    }

    private SIPMessage generateJainSipMessage(String strSipMessage) throws ParseException {
        return stringMsgParser.parseSIPMessage(strSipMessage.getBytes(), true, false, null);
    }


    // TODO: Refactoring -> 'proxy' app으로 이동
    /**
     * Set ServerReflexive address to Via 'rport', 'received'
     * @param nettyInbound
     * @param jainSipMessage
     * @return
     * @throws ParseException
     */
    private SIPMessage updateMessage(NettyInbound nettyInbound, SIPMessage jainSipMessage) throws ParseException {

        if(jainSipMessage instanceof SIPRequest){
            ViaList viaList = jainSipMessage.getViaHeaders();

            Via topViaHeader=(Via) viaList.getFirst();

            if (topViaHeader.getReceived() == null) {
                String received = ((InetSocketAddress) nettyInbound.remoteAddress()).getHostString();
                topViaHeader.setReceived(received);
            }

            if(topViaHeader.getRPort() == 0 || topViaHeader.getRPort() == -1) {
                int rport = ((InetSocketAddress) nettyInbound.remoteAddress()).getPort();

                topViaHeader.setParameter("rport", rport+"");
            }

            viaList.set(0, topViaHeader);
            jainSipMessage.setHeader(viaList);
        }

        return jainSipMessage;
    }

    private DefaultSipMessage generateGeneralSipMessage(NettyInbound nettyInbound, SIPMessage jainSipMessage){
        DefaultSipMessage defaultSipMessage = null;
//
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
