package org.lunker.new_proxy.sip.wrapper.message.proxy;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPResponse;
import org.lunker.new_proxy.sip.wrapper.message.DefaultSipResponse;
import org.lunker.new_proxy.sip.wrapper.message.Sessionable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class ProxySipResponse extends DefaultSipResponse implements Sessionable {
    private Logger logger= LoggerFactory.getLogger(ProxySipResponse.class);

    private ProxySipMessage relatedRequest=null;

    public ProxySipResponse(SIPMessage jainSipResponse) {
        super(jainSipResponse);
    }

    public ProxySipMessage createACK() {

        /**
         * create ack
         *
         * 0) copy header
         * 1) copy via
         * 2) copy from
         * 3) copy to
         * 4) copy route-header
         */

        /*
        SIPResponse sipResponse = (SIPResponse) this.message;
        SIPRequest ackRequest=new SIPRequest();

        ackRequest.setMethod(Request.ACK);
        ackRequest.setRequestURI();
        ackRequest.setCallId();
        ackRequest.setCSeq();

        // copy via
        List<Via> vias=new ArrayList<>();
        Via lastResponseTopMostVia=null;

        lastResponseTopMostVia=sipResponse.getTopmostVia();
        lastResponseTopMostVia.removeParameters();

        NameValueList originalRequestParameters=this.relatedRequest.getTopmostVia().getParameters();
        if(originalRequestParameters != null) {

        }

        lastResponseTopMostVia.setParameter(relatedRequest.);

        ackRequest.setVia(vias);

        this.sipMessageFactory.getMessageFactory().createRequest();
        */


        /*
        if (logger.isDebugEnabled()) {
            logger.debug("transaction " + this.getTransaction());
            logger.debug("originalRequest " + this.originalRequest);
        }

        if ((this.getTransaction() != null || this.originalRequest == null || "INVITE".equals(this.getMethod()) || "INVITE".equals(this.originalRequest.getMethod())) && (this.getTransaction() == null || "INVITE".equals(((SIPTransaction) this.getTransaction()).getMethod())) && (response.getStatusCode() < 100 || response.getStatusCode() >= 200) && !this.isAckGenerated) {
            MobicentsSipSession session = this.getSipSession();
            Dialog dialog = session.getSessionCreatingDialog();
            CSeqHeader cSeqHeader = (CSeqHeader) response.getHeader("CSeq");
            SipServletRequestImpl sipServletAckRequest = null;

            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("dialog to create the ack Request " + dialog);
                }

                Request ackRequest = dialog.createAck(cSeqHeader.getSeqNumber());
                ackRequest.removeHeader("Via");
                if (logger.isInfoEnabled()) {
                    logger.info("ackRequest just created " + ackRequest);
                }

                ListIterator<RouteHeader> routeHeaders = ackRequest.getHeaders("Route");
                ackRequest.removeHeader("Route");

                while (true) {
                    RouteHeader routeHeader;
                    String serverId;
                    String routeAppName;
                    do {
                        if (!routeHeaders.hasNext()) {
                            sipServletAckRequest = (SipServletRequestImpl) this.sipFactoryImpl.getMobicentsSipServletMessageFactory().createSipServletRequest(ackRequest, this.getSipSession(), this.getTransaction(), dialog, false);
                            this.isAckGenerated = true;
                            return sipServletAckRequest;
                        }

                        routeHeader = (RouteHeader) routeHeaders.next();
                        serverId = ((SipURI) routeHeader.getAddress().getURI()).getParameter("as");
                        String routeAppNameHashed = ((SipURI) routeHeader.getAddress().getURI()).getParameter("appname");
                        routeAppName = null;
                        if (routeAppNameHashed != null) {
                            routeAppName = this.sipFactoryImpl.getSipApplicationDispatcher().getApplicationNameFromHash(routeAppNameHashed);
                        }
                    }
                    while (routeAppName != null && this.sipFactoryImpl.getSipApplicationDispatcher().getApplicationServerId().equalsIgnoreCase(serverId) && routeAppName.equals(this.getSipSession().getKey().getApplicationName()));

                    ackRequest.addHeader(routeHeader);
                }
            } catch (InvalidArgumentException var12) {
                logger.error("Impossible to create the ACK", var12);
            } catch (SipException var13) {
                logger.error("Impossible to create the ACK", var13);
            }

            return sipServletAckRequest;
        }
        */


        /*
        {

            SIPRequest sipRequest = new SIPRequest();
            sipRequest.setMethod(Request.ACK);
            sipRequest.setRequestURI((SipUri) getRemoteTarget().getURI()
                    .clone());
            sipRequest.setCallId(this.getCallId());
            sipRequest.setCSeq(new CSeq(cseqno, Request.ACK));


            List<Via> vias = new ArrayList<Via>();
            // Via via = lp.getViaHeader();
            // The user may have touched the sentby for the response.
            // so use the via header extracted from the response for the ACK =>
            // https://jain-sip.dev.java.net/issues/show_bug.cgi?id=205
            // strip the params from the via of the response and use the params
            // from the
            // original request
            Via via = this.lastResponseTopMostVia;
            if (logger.isLoggingEnabled(LogWriter.TRACE_DEBUG)) {
                logger.logDebug("lastResponseTopMostVia " + lastResponseTopMostVia);
            }
            via.removeParameters();
            if (originalRequest != null
                    && originalRequest.getTopmostVia() != null) {
                NameValueList originalRequestParameters = originalRequest
                        .getTopmostVia().getParameters();
                if (originalRequestParameters != null
                        && originalRequestParameters.size() > 0) {
                    via.setParameters((NameValueList) originalRequestParameters
                            .clone());
                }
            }
            via.setBranch(Utils.getInstance().generateBranchId()); // new branch
            vias.add(via);
            if (logger.isLoggingEnabled(LogWriter.TRACE_DEBUG)) {
                logger.logDebug("Adding via to the ACK we are creating : " + via + " lastResponseTopMostVia " + lastResponseTopMostVia);
            }
            sipRequest.setVia(vias);

            From from = new From();
            from.setAddress(this.getLocalParty());
            from.setTag(this.myTag);
            sipRequest.setFrom(from);
            To to = new To();
            to.setAddress(this.getRemoteParty());
            if (hisTag != null)
                to.setTag(this.hisTag);
            sipRequest.setTo(to);
            sipRequest.setMaxForwards(new MaxForwards(70));

            if (this.originalRequest != null) {
                Authorization authorization = this.originalRequest
                        .getAuthorization();
                if (authorization != null)
                    sipRequest.setHeader(authorization);
                // jeand : setting back the original Request to null to avoid
                // keeping references around for too long
                // since it is used only in the dialog setup
                originalRequestRecordRouteHeaders = originalRequest
                        .getRecordRouteHeaders();
                originalRequest = null;
            }
        }
        */
        return null;
    }// end method


    @Override
    public Object clone() {
        SIPResponse sipResponse=(SIPResponse) this.message.clone();
        return new ProxySipResponse(sipResponse);
    }
}
