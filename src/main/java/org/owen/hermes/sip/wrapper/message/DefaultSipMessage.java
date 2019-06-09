package org.owen.hermes.sip.wrapper.message;

import java.net.InetSocketAddress;

import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import lombok.extern.slf4j.Slf4j;

import org.owen.hermes.sip.util.SipMessageFactory;

import gov.nist.javax.sip.header.Authorization;
import gov.nist.javax.sip.header.RecordRouteList;
import gov.nist.javax.sip.header.RouteList;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.header.ViaList;
import gov.nist.javax.sip.message.SIPMessage;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

/**
 *
 * jain sip message wrapper for general purpose
 * Base class for LB, Proxy SipMessage
 * Created by dongqlee on 2018. 4. 26..
 */
@Slf4j
public abstract class DefaultSipMessage {

    protected SipMessageFactory sipMessageFactory;
    protected SIPMessage message;
    protected String method;

    public static DefaultSipMessage empty(){
        return DefaultSipMessageEmpty.getInstance();
    }

    public DefaultSipMessage(){

    }

    public DefaultSipMessage(SIPMessage sipMessage) {
        this.message=sipMessage;
        this.sipMessageFactory=SipMessageFactory.getInstance();
    }

    public String getMethod(){
        if (this.method == null) {
            this.method = this.message instanceof Request ? ((Request)this.message).getMethod() : ((CSeqHeader)this.message.getHeader("CSeq")).getMethod();
        }

        return this.method;
    }

    /**
     * Get 'From' Header
     * @return
     * @throws NullPointerException
     */
    public FromHeader getFrom() throws NullPointerException{
        if(this.message==null)
            throw new NullPointerException("");
        return this.message.getFrom();
    }

    public ToHeader getTo() throws NullPointerException{
        if(this.message==null)
            throw new NullPointerException("");
        return this.message.getTo();
    }

    public String getCallId() {
        CallIdHeader id = (CallIdHeader)this.message.getHeader("Call-ID");
        return id != null ? id.getCallId() : null;
    }

    public void addHeader(Header header){
        this.message.addHeader(header);
    }

    public Header getHeader(String headerName) throws NullPointerException{
        if(this.message==null)
            throw new NullPointerException("");

        return this.message.getHeader(headerName);
    }

    public ViaList getViaHeaders(){
        return this.message.getViaHeaders();
    }

    public Via getTopmostVia(){
        return this.message.getTopmostVia();
    }

    public Authorization getAuthorization(){
        return this.message.getAuthorization();
    }

    public SIPMessage getRawSipMessage(){
        return this.message;
    }

    /**
     * send msg to specific node.
     * if connection manager has channel already, use it.
     * if not create new channel and add to channel to connection manager for the future use.
     * @param remoteHost
     * @param remotePort
     * @param remoteTransport
     */
    public void send(String remoteHost, int remotePort, String remoteTransport, Class SipMessageHandlerImpl) throws Exception {
        ChannelHandlerContext targetCtx = null;

        // TODO: change name client to something like node?
//        targetCtx = this.connectionManager.getConnection(remoteHost, remotePort, remoteTransport);

        if (targetCtx != null) { // found channel
            ChannelFuture cf;
            switch (remoteTransport.toLowerCase()) {
                case "udp":
                    cf = targetCtx.writeAndFlush(new DatagramPacket(
                            Unpooled.copiedBuffer(this.message.toString(), CharsetUtil.UTF_8),
                            new InetSocketAddress(remoteHost, remotePort)
                    ));
                    break;
                default:
                    cf = targetCtx.writeAndFlush((Unpooled.copiedBuffer(this.message.toString(), CharsetUtil.UTF_8))).sync();
                    break;
            }
            // logging
            ChannelHandlerContext finalTargetCtx = targetCtx;
            cf.addListener((future) -> {
                if (future.isSuccess())
                    log.info(String.format("[Success] Send message from %s to %s\n%s\n", finalTargetCtx.channel().localAddress(),
                                         finalTargetCtx.channel().remoteAddress(), this.message));
                else
                    log.info(String.format("[Fail] Send message from %s to %s\n%s\nfailed cause : %s", finalTargetCtx.channel().localAddress(),
                                         finalTargetCtx.channel().remoteAddress(), this.message, future.cause()));
            });
        }
    }

    public MaxForwardsHeader getMaxForwards(){
        return this.message.getMaxForwards();
    }

    public CSeqHeader getCSeq(){
        return this.message.getCSeq();
    }

    public RouteList getRouteHeaders(){
        return this.message.getRouteHeaders();
    }

    public RecordRouteList getRecordRouteHeaders(){
        return this.message.getRecordRouteHeaders();
    }

    protected Response createResponse(int responseCode, Request baseRequest){


        return null;
    }

    @Override
    public String toString() {
        return this.message.toString();
    }
}
