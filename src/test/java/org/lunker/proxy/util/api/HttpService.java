package org.lunker.proxy.util.api;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.util.EntityUtils;
import org.lunker.proxy.util.HttpEntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class HttpService {
    private static HttpService instance=null;
    private Logger logger= LoggerFactory.getLogger(HttpService.class);
    private PoolingHttpClientConnectionManager syncConnectionManager=null;
    private PoolingNHttpClientConnectionManager asyncConnectionManager=null;

    // Test REST URl
    public String restEndpoint="http://203.240.153.14:8180/b2b/v1.0";

    private HttpService(){
        try{
            syncConnectionManager=new PoolingHttpClientConnectionManager();
            syncConnectionManager.setMaxTotal(500);
            syncConnectionManager.setDefaultMaxPerRoute(100);

            asyncConnectionManager=new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor());
            asyncConnectionManager.setMaxTotal(1000);
            asyncConnectionManager.setDefaultMaxPerRoute(100);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static HttpService getInstance() {
        if (instance==null)
            instance=new HttpService();
        return instance;
    }

    public <T extends Object> T get(String url, Class<T> type) throws IOException{
        logger.info("Generate http GET request for url : " + url);

        T response=null;
        CloseableHttpClient httpClient=HttpClients.custom().setConnectionManager(syncConnectionManager).build();
        String requestUrl=restEndpoint+url;

        HttpGet httpGet=new HttpGet(requestUrl);
        CloseableHttpResponse closeableHttpResponse=httpClient.execute(httpGet);
        HttpEntity httpEntity=closeableHttpResponse.getEntity();

        String contentType=closeableHttpResponse.getFirstHeader("Content-Type").getValue();

        if(contentType.equals("application/xml")){
            response=(T) EntityUtils.toString(httpEntity);
        }
        else {
            response=(T) HttpEntityUtils.toJson(httpEntity);
        }

//        closeableHttpResponse.close();
//        EntityUtils.consume(httpEntity);

        return response;
    }

    public <T extends Object> T getAsync(String url, Class<T> type) throws InterruptedException, ExecutionException, IOException{

        T response=null;

        CloseableHttpAsyncClient client = HttpAsyncClients.custom().setConnectionManager(asyncConnectionManager).build();

        client.start();

        String requestUrl=restEndpoint+url;

        HttpGet request = new HttpGet(requestUrl);

        Future<HttpResponse> future = client.execute(request, null);

        HttpResponse httpResponse = future.get();
        HttpEntity httpEntity=httpResponse.getEntity();

        String contentType=httpResponse.getFirstHeader("Content-Type").getValue();

        if(contentType.equals("application/xml")){
            response=(T) EntityUtils.toString(httpEntity);
        }
        else {
            response=(T) HttpEntityUtils.toJson(httpEntity);
        }

        client.close();
//        EntityUtils.consume(httpEntity);

        return response;
    }

    public String post(){
//        logger.info("Generate http POST request for url : " + url);
//
//        String strResponse="";
//        CloseableHttpClient httpClient= HttpClients.createDefault();
//
//        HttpPost httpPost=new HttpPost(url);
//        StringEntity requestEntity = new StringEntity(
//                data.toString(),
//                ContentType.APPLICATION_JSON);
//        httpPost.setEntity(requestEntity);
//
//        CloseableHttpResponse response=httpClient.execute(httpPost);
//        HttpEntity httpEntity=response.getEntity();
//
//        String contentType=response.getFirstHeader("Content-Type").getValue();
//
//        if(contentType.equals("application/xml")){
//            logger.info("xml!");
//            strResponse= EntityUtils.toString(httpEntity);
//        }
//        else {
//            logger.info("json!");
//            strResponse= EntityUtils.toString(httpEntity);
//        }
//
//        EntityUtils.consume(httpEntity);
//
//        return strResponse;

        return "";
    }
}
