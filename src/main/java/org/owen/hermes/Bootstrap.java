package org.owen.hermes;

import io.netty.channel.ChannelFuture;
import org.owen.hermes.config.Configuration;
import org.owen.hermes.model.ServerInfo;
import org.owen.hermes.model.Transport;
import org.owen.hermes.stub.SipServer;
import org.owen.hermes.stub.SipMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

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
    ]

    public static void startServer(String host, int port, Transport transport, Class<? extends SipMessageHandler> sipMessageHandlerClass){
        ServerInfo serverInfo=new ServerInfo(host, port, transport);
        SipServer serverThread=null;

        sipMessageHandlerClass.getcon

        serverThread=generateServerThread(serverInfo, sipMessageHandlerClass);
    }
}
