package org.lunker.new_proxy.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.lunker.new_proxy.core.constants.ServerType;
import org.lunker.new_proxy.exception.InvalidConfigurationException;
import org.lunker.new_proxy.model.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class Configuration {
    private Logger logger= LoggerFactory.getLogger(Configuration.class);

    private static JsonObject configurationJson=null;

    private ServerType serverType=ServerType.NONE;
    private Map<String, Object> tcpConfigMap=null;
    private Map<String, Object> udpConfigMap=null;
    private Map<String, Object> wsConfigMap=null;
    private Map<String, Object> wssConfigMap=null;

    private static final String TRANSPORT_HOST="host";
    private static final String TRANSPORT_PORT="port";

    private static final String TRANSPORT_TCP="tcp";
    private static final String TRANSPORT_UDP="udp";
    private static final String TRANSPORT_TLS="tls";{}
    private static final String TRANSPORT_HTTP="http";
    private static final String TRANSPORT_WS="ws";
    private static final String TRANSPORT_WSS="wss";

    boolean isValidServerType=false;
    boolean isValidTCP=false;
    boolean isValidUDP=false;
    boolean isValidTLS=false;
    boolean isValidHTTP=false;
    boolean isValidWS=false;
    boolean isValidWSS=false;


    public static Configuration getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private Configuration() throws RuntimeException{
        // TCP Transport config
        tcpConfigMap=new HashMap<>();

        // UDP Transport config
        udpConfigMap=new HashMap<>();

        // WS Transport config
        wsConfigMap=new HashMap<>();

        // WSS Transport config
        wssConfigMap=new HashMap<>();

        try{
            InputStream in=getClass().getResourceAsStream("/application.json");

            if(in.available() != 0){
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder stringBuilder=new StringBuilder();
                reader.lines().forEach((line)->{
                    stringBuilder.append(line);
                });

                try{
                    String content = stringBuilder.toString();
                    JsonParser jsonParser=new JsonParser();

                    configurationJson=jsonParser.parse(content).getAsJsonObject();

                    deserialize();
                }
                catch (Exception e){
//            e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
            else{
                throw new RuntimeException("Server configuration file is not exist. Put 'application.json' under resources dir");
            }

        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void deserialize() throws InvalidConfigurationException {
        this.serverType=ServerType.convert(configurationJson.get("type").getAsString());
        if(this.serverType==ServerType.NONE)
            isValidServerType=false;
        else
            isValidServerType=true;

        if(!isValidServerType)
            throw new InvalidConfigurationException("Configuration 'ServerType' is not correct");

        JsonObject transportConfig=configurationJson.getAsJsonObject("transport");

        // TCP Server
        if(transportConfig.has(TRANSPORT_TCP)){
            JsonObject tcpJsonConfig=null;
            tcpJsonConfig=transportConfig.getAsJsonObject(TRANSPORT_TCP);

            if(validate(tcpJsonConfig)){
                setConfigMap(tcpConfigMap, tcpJsonConfig);
                isValidTCP=true;
            }
            else
                throw new InvalidConfigurationException("Configuration 'TCP' options is not correct");
        }
        // UDP Server
        if(transportConfig.has(TRANSPORT_UDP)){
            JsonObject udpJsonConfig = null;
            udpJsonConfig = transportConfig.getAsJsonObject(TRANSPORT_UDP);

            if (validate(udpJsonConfig)) {
                setConfigMap(udpConfigMap, udpJsonConfig);
                isValidUDP = true;
            }
            else
                throw new InvalidConfigurationException("Configuration 'UDP' options is not correct");
        }

        if(transportConfig.has(TRANSPORT_TLS)){

        }

        if(transportConfig.has(TRANSPORT_HTTP)){

        }

        if(transportConfig.has(TRANSPORT_WS)){
            JsonObject wsJsonConfig= null;
            wsJsonConfig = transportConfig.getAsJsonObject(TRANSPORT_WS);

            if (validate(wsJsonConfig)) {
                setConfigMap(wsConfigMap, wsJsonConfig);
                isValidWS = true;
            }
            else
                throw new InvalidConfigurationException("Configuration 'WS' options is not correct");
        }

        if(transportConfig.has(TRANSPORT_WSS)){
            JsonObject wssJsonConfig= null;
            wssJsonConfig = transportConfig.getAsJsonObject(TRANSPORT_WSS);

            if (validate(wssJsonConfig)) {
                setConfigMap(wssConfigMap, wssJsonConfig);
                isValidWS = true;
            }
            else
                throw new InvalidConfigurationException("Configuration 'WS' options is not correct");
        }

    }

    private boolean validate(JsonObject config){
        if(config.has(TRANSPORT_HOST) && config.has(TRANSPORT_PORT))
            return true;
        else
            return false;
    }

    private void setConfigMap(Map<String, Object> configMap, JsonObject jsonConfig){
        Iterator<Map.Entry<String, JsonElement>> iterator=jsonConfig.entrySet().iterator();
        Map.Entry<String, JsonElement> entry=null;

        String key="";
        Object value=null;

        while(iterator.hasNext()){
            entry=iterator.next();

            try {
                key=entry.getKey();
                value=entry.getValue();

                if(value instanceof JsonObject){
                    Map<String, Object> childConfig=new HashMap<>();

                    setConfigMap(childConfig, ((JsonObject) value).getAsJsonObject());
                    configMap.put(key, childConfig);
                }
                else {
                    if(((JsonPrimitive)value).isNumber()){
//                        value=((JsonElement) value).getAsInt();
                        configMap.put(key, ((JsonElement) value).getAsInt());
                    }
                    else if(((JsonPrimitive)value).isString()){
                        value=((JsonElement) value).getAsString();
                        configMap.put(key, value);
                    }
                    else if(((JsonPrimitive)value).isBoolean()){
                        value=((JsonElement) value).getAsBoolean();
                        configMap.put(key, value);
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public JsonObject get(){
        return this.configurationJson;
    }


    public boolean isValidTCP() {
        return isValidTCP;
    }

    public boolean isValidUDP() {
        return isValidUDP;
    }

    public boolean isValidTLS() {
        return isValidTLS;
    }

    public boolean isValidHTTP() {
        return isValidHTTP;
    }

    public boolean isValidWS() {
        return isValidWS;
    }

    public boolean isValidWSS() {
        return isValidWSS;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public Map<String, Object> getConfigMap(Transport transport){
        if(Transport.TCP.equals(transport)){
            return this.tcpConfigMap;
        }
        else if(Transport.UDP.equals(transport)){
            return this.udpConfigMap;
        }
        else if(Transport.WS.equals(transport)){
            return this.wsConfigMap;
        }
        else if(Transport.WSS.equals(transport)){
            return this.wssConfigMap;
        }
        else {
            return null;
        }
        /*
        TODO:
        else if(Transport.TLS.equals(transport)){

        }
        else if(Transport.WS.equals(transport)){

        }
        */
    }

    public Map<String, Object> getTcpConfigMap() {
        return tcpConfigMap;
    }

    public Map<String, Object> getUdpConfigMap() {
        return udpConfigMap;
    }


    private static class SingletonHolder{
        private static Configuration INSTANCE=new Configuration();
    }
}
