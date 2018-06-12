package org.lunker.new_proxy.server.udp;

import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.header.ViaList;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.parser.StringMsgParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.lunker.new_proxy.core.ConnectionManager;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipMessage;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipRequest;
import org.lunker.new_proxy.sip.wrapper.message.proxy.ProxySipResponse;
import org.lunker.new_proxy.stub.SipMessageHandler;
import org.lunker.new_proxy.util.lambda.StreamHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.text.ParseException;
import java.util.Optional;


public class UdpServerHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(UdpServerHandler.class);
    private StringMsgParser stringMsgParser = null;
    private DatagramPacket receivedPacket = null;
    Optional<SipMessageHandler> optionalSipMessageHandler = null;
    private ConnectionManager connectionManager = ConnectionManager.getInstance();

    public UdpServerHandler(Optional<SipMessageHandler> optionalSipMessageHandler) {
        this.stringMsgParser = new StringMsgParser();
        this.optionalSipMessageHandler = optionalSipMessageHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            this.receivedPacket = (DatagramPacket) msg;

            this.connectionManager.addConnection(
                    this.receivedPacket.sender().getHostString(),
                    this.receivedPacket.sender().getPort(),
                    "udp",
                    ctx);

            Mono<String> wrapper = Mono.fromCallable(() -> {
                Optional<String> maybeStrSipMessage = Optional.ofNullable(this.receivedPacket.content().toString(CharsetUtil.UTF_8));
                Optional<DefaultSipMessage> maybeGeneralSipMessage = deserialize(ctx, maybeStrSipMessage);

                this.optionalSipMessageHandler.get().handle(maybeGeneralSipMessage);


//                ctx.writeAndFlush(new DatagramPacket(
//                        Unpooled.copiedBuffer(maybeGeneralSipMessage.get().toString(), CharsetUtil.UTF_8),
//                        this.receivedPacket.sender()));

                return "fromCallable return value";
            });
            wrapper = wrapper.subscribeOn(Schedulers.parallel());
            wrapper.subscribe();


        } catch (Exception e) {
            logger.warn("Error while encoding sip wrapper . . . :\n" + ((Optional<String>) msg).get());
            ctx.fireExceptionCaught(e);
        }

    }

    private Optional<DefaultSipMessage> deserialize(ChannelHandlerContext ctx, Optional<String> maybeStrSipMessage) {
        return maybeStrSipMessage
                .map(StreamHelper.wrapper(strSipMessage -> generateJainSipMessage(strSipMessage)))
                .map(StreamHelper.wrapper(jainSipMessage -> updateMessage(ctx, jainSipMessage)))
                .map(jainSipMessage -> generateGeneralSipMessage(ctx, jainSipMessage));
    }

    private DefaultSipMessage generateGeneralSipMessage(ChannelHandlerContext ctx, SIPMessage jainSipMessage) {
        DefaultSipMessage defaultSipMessage = null;

        if (jainSipMessage instanceof SIPRequest) {
            defaultSipMessage = new ProxySipRequest(jainSipMessage);
        } else {
            defaultSipMessage = new ProxySipResponse(jainSipMessage);
        }

        return defaultSipMessage;
    }

    private SIPMessage updateMessage(ChannelHandlerContext ctx, SIPMessage jainSipMessage) throws ParseException {
        ViaList viaList = jainSipMessage.getViaHeaders();
        Via topViaHeader = (Via) viaList.getFirst();

        if (topViaHeader.getReceived() == null) {
            topViaHeader.setReceived(this.receivedPacket.sender().getHostString());
        }

        if (topViaHeader.getRPort() == 0 || topViaHeader.getRPort() == -1) {
            topViaHeader.setParameter("rport", this.receivedPacket.sender().getPort() + "");
        }

        viaList.set(0, topViaHeader);
        jainSipMessage.setHeader(viaList);

        return jainSipMessage;
    }

    private SIPMessage generateJainSipMessage(String strSipMessage) throws ParseException {
        return stringMsgParser.parseSIPMessage(strSipMessage.getBytes(), true, false, null);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
//        ctx.close();
    }
}
