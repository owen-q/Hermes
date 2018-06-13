package org.owen.hermes.stub;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public abstract class SipServer {
//    protected TransportChannelInitializer channelInitializer=null;
//    protected Map<String, Object> transportConfigMap=null;
    protected boolean SYNC=true;
    protected boolean ASYNC=false;

    public void runSync() throws Exception{
        run(SYNC);
    }

    public void runAsync() throws Exception{
        run(ASYNC);
    }


    abstract public void run(boolean isSync) throws Exception;
}
