package org.owen.proxy;

import org.owen.hermes.Bootstrap;
import org.owen.hermes.model.Transport;
import org.owen.proxy.sip.SipServletImpl;

/**
 * Created by dongqlee on 2018. 5. 9..
 */
public class Application {
    public static void main(String[] args){
        try{
            Bootstrap.addHandler(Transport.TCP, SipServletImpl.class);
//            Bootstrap.addHandler(Transport.UDP, SipServletImpl.class);
//            Bootstrap.addHandler(Transport.WS, SipServletImpl.class);
//            Bootstrap.addHandler(Transport.WSS, SipServletImpl.class);
            Bootstrap.run();
        }
        catch (Exception e ){
            e.printStackTrace();
        }
    }
}
