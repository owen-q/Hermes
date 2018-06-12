package org.lunker.new_proxy.sip.processor;

import org.lunker.new_proxy.core.constants.ServerType;
import org.lunker.new_proxy.model.ServerInfo;
import org.lunker.new_proxy.stub.SipMessageHandler;

/**
 * Created by dongqlee on 2018. 4. 27..
 */
public class ServerProcessor {
//    private ServerInfo serverInfo=null;
//
//    private PreProcessor preProcessor=null;
//    private SipMessageHandler sipMessageHandler=null;
//    private PostProcessor postProcessor=null;
//
//    private String sipMessageHandlerClassName="";
//
//    public ServerProcessor() {
//    }
//
//    public ServerInfo getServerInfo() {
//        return serverInfo;
//    }
//
//    public void setServerInfo(ServerInfo serverInfo) {
//        this.serverInfo = serverInfo;
//    }
//
//    public String getSipMessageHandlerClassName() {
//        return sipMessageHandlerClassName;
//    }
//
//    public void setPreProcessor(PreProcessor preProcessor) {
//        this.preProcessor = preProcessor;
//    }
//
//    public PostProcessor getPostProcessor() {
//        return postProcessor;
//    }
//
//    public SipMessageHandler getSipMessageHandler() {
//        return sipMessageHandler;
//    }
//
//    public void setSipMessageHandler(SipMessageHandler sipMessageHandler) {
//        this.sipMessageHandler = sipMessageHandler;
//    }
//
//    //TODO: Refactoring
//    public void setSipMessageHandlerClassName(String sipMessageHandlerClassName) throws ClassNotFoundException, IllegalAccessException, InstantiationException{
//        this.sipMessageHandlerClassName = sipMessageHandlerClassName;
//
//        try{
//            this.sipMessageHandler=(SipMessageHandler) Class.forName(this.sipMessageHandlerClassName).getConstructor(ServerInfo.class).newInstance(this.serverInfo);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public void setPostProcessor(PostProcessor postProcessor) {
//        this.postProcessor = postProcessor;
//    }
//
//    // TODO: Refactoring
//    public PreProcessor newPreProcessorInstance() {
//        if(serverInfo.getServerType() == ServerType.LB){
//            return new LoadBalancerPreProcessor(this.sipMessageHandler);
//        }
//        else{
//            return new ProxyPreProcessor(this.sipMessageHandler);
//        }
//    }
//
//    public PreProcessor getPreProcessor() {
//        return preProcessor;
//    }
}
