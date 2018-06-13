package org.owen.hermes.stub;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.owen.hermes.server.TransportChannelInitializer;

import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public abstract class SipServer extends ChannelInboundHandlerAdapter{
    protected TransportChannelInitializer channelInitializer=null;
    protected Map<String, Object> transportConfigMap=null;
    protected boolean SYNC=true;
    protected boolean ASYNC=false;

    public ChannelFuture runSync() throws Exception{
        return run(SYNC);
    }

    public ChannelFuture runAsync() throws Exception{
        return run(ASYNC);
    }

    abstract public ChannelFuture run(boolean isSync) throws Exception;
}
