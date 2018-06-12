package org.lunker.new_proxy.core.constants;

/**
 * Created by dongqlee on 2018. 4. 27..
 */
public enum ServerType {
    NONE("none"),
    PROXY("proxy"),
    LB("lb");

    private String value;

    ServerType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static ServerType convert(String strServerType){
        if(strServerType.equals(ServerType.LB.value))
            return ServerType.LB;
        else if(strServerType.equals(ServerType.PROXY.value))
            return ServerType.PROXY;
        else
            return ServerType.NONE;
    }
}
