package org.lunker.new_proxy;

import io.netty.channel.ChannelFuture;
import org.lunker.new_proxy.config.Configuration;
import org.lunker.new_proxy.core.constants.ServerType;
import org.lunker.new_proxy.exception.BootstrapException;
import org.lunker.new_proxy.exception.InvalidConfigurationException;
import org.lunker.new_proxy.model.ServerInfo;
import org.lunker.new_proxy.model.Transport;
import org.lunker.new_proxy.sip.processor.ServerProcessor;
import org.lunker.new_proxy.stub.AbstractServer;
import org.lunker.new_proxy.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Server Application Bootstrap
 * Created by dongqlee on 2018. 4. 26..
 */
public class Bootstrap {
    private static Logger logger=LoggerFactory.getLogger(Bootstrap.class);
    private static Configuration configuration=Configuration.getInstance();
    // Store server threads for multiple instance
    private static List<Mono<ChannelFuture>> serverList=new ArrayList<>();

    public static void addHandler(Transport transport, Class sipMessageHandlerImplClass) throws BootstrapException {
        addHandler(transport, sipMessageHandlerImplClass.getName());
    }

    // ISSUE:tcp, udp 등 여러 서버들간에 동일한 Handler 객체를 넘겨줘도 되는가? 아니면 각각 서버들마다 다른 객체를 넘겨줘야하나?
    public static void addHandler(Transport transport, String sipMessageHandlerImplClassName) throws BootstrapException {
        try{
            Mono<ChannelFuture> serverThread=null;
            ServerInfo serverInfo = new ServerInfo(
                    configuration.getServerType(), // server type
                    (String)configuration.getConfigMap(transport).get("host"), // host
                    (int)configuration.getConfigMap(transport).get("port"), // port
                    transport); // transport
            ServerProcessor serverProcessor=null;

            serverThread=generateServerThread(transport, (SipMessageHandler) Class.forName(sipMessageHandlerImplClassName).getConstructor(ServerInfo.class).newInstance(serverInfo));

            serverList.add(serverThread);
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error("Failed to start [{}] server.", transport);
            throw new BootstrapException(e.getMessage());
        }
    }

    // TODO
    public static void addShutdownHandler(){
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                System.out.println("Shutdown hook ran!");
            }
        });
    }

    public static void run() throws Exception{
        Mono<ChannelFuture> serverMono=null;
        ChannelFuture result=null;

        for(int idx=0; idx<serverList.size(); idx++){
            final int cnt=idx;
            serverMono=serverList.get(idx);

            // make last server sync for block main thread
            serverMono.subscribe((channelFuture)->{
                if(cnt==serverList.size()-1){
                    try{
                        channelFuture.channel().closeFuture().await();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    // TODO: change mono->
    private static Mono<ChannelFuture> generateServerThread(Transport transport, SipMessageHandler sipMessageHandler){
        Mono<ChannelFuture> serverThread=Mono.fromCallable(()->{
            AbstractServer server=null;
            ChannelFuture f=null;

            if(logger.isDebugEnabled())
                logger.debug("[{}] Server starting ...", transport);

            // Create Server instance
            server=AbstractServer.create(transport, sipMessageHandler);

            try{
                // Run server
                f=server.run();
                if(logger.isDebugEnabled())
                    logger.debug("[{}] Server started", transport);
            }
            catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException(String.format("[%s] Server starting error. cause, %s", transport));
            }

            return f;
        });

        serverThread.subscribeOn(Schedulers.newElastic("elastic-server-" + transport));
        return serverThread;
    }
}
