package org.lunker.proxy.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

/**
 * Created by dongqlee on 2018. 3. 19..
 */
public class JedisConnection {
    private JedisPool jedisPool=null;
    private String redisHost="10.0.1.159";
    private int redisPort=6379;
    private int dbIndex=0;


    private JedisConnection() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(400);
        config.setMaxIdle(1000 * 60 * 60 * 6);
        config.setMaxWaitMillis(15 * 1000);

        jedisPool = new JedisPool(config, redisHost, redisPort);
    }

    public static JedisConnection getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public void set(String key, String value){
        Jedis jedis=null;

        try{
            jedis=jedisPool.getResource();

            Pipeline pipeline=jedis.pipelined();

            pipeline.set(key, value);
            pipeline.sync();


            jedis.close();
            pipeline.close();
            jedis=null;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(jedis!=null)
                jedis.close();
        }
    }

    public void close(){
        try{
            if(jedisPool!=null)
                jedisPool.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static class SingletonHolder{
        private static JedisConnection INSTANCE=new JedisConnection();
    }
}
