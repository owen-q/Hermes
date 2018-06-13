package org.owen.proxy.registrar;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dongqlee on 2018. 3. 16..
 *
 *
 *
 */
public class Registrar {
    private int REGISTRAR_CAPACITY=50000;
    // key: aor
    // value: Registration info
    private Map<String, Registration> registrationMap=null;
//    private Map<>

//    private Map<String, ChannelHandlerContext> ctxMap;

    private Registrar() {
        registrationMap=new ConcurrentHashMap<>(REGISTRAR_CAPACITY);
    }

    public static Registrar getInstance(){
        return SingletonHolder.INSTANCE;
    }

    /**
     *
     * @param userKey AOR
     * @param registration
     */
    public void register(String userKey, Registration registration){
        registrationMap.put(userKey, registration);
    }

    public Registration getRegistration(String userKey){
        return registrationMap.get(userKey);
    }

    private static class SingletonHolder{
        private static Registrar INSTANCE=new Registrar();
    }
}
