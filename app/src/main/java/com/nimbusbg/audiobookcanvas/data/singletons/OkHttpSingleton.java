package com.nimbusbg.audiobookcanvas.data.singletons;

import android.content.Context;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpSingleton
{
    private static OkHttpSingleton instance;
    private static OkHttpClient client;
    
    private static Context ctx;
    private static String cacheName;
    private static int cacheSizeMb;
    private static int numThreads;
    
    private OkHttpSingleton(Context context)
    {
        ctx = context;
        cacheName = "okhttp";
        cacheSizeMb = 1;
        numThreads = Runtime.getRuntime().availableProcessors();
        client = getClient();
    }
    
    public static synchronized OkHttpSingleton getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new OkHttpSingleton(context);
        }
        return instance;
    }
    
    public OkHttpClient getClient()
    {
        if(client == null)
        {
            Dispatcher requestDispatcher = new Dispatcher();
            requestDispatcher.setMaxRequestsPerHost(numThreads);
            
            client = new OkHttpClient().newBuilder()
                    .readTimeout(50, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .connectTimeout(2,TimeUnit.SECONDS)
                    .connectionPool(new ConnectionPool(numThreads,5,TimeUnit.SECONDS))
                    .cache(new Cache(new File(ctx.getCacheDir(), cacheName), cacheSizeMb * 1024 * 1024))
                    .dispatcher(requestDispatcher)
                    .build();
        }
        return client;
    }

    public Request createRequest(String url, String requestBody, String apiKey)
    {
        return new Request.Builder()
                .url(url)
                .post(RequestBody.create(requestBody, MediaType.parse("application/json; charset=utf-8")))
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
    }
    
    public void cancelAllRequests()
    {
        getClient().dispatcher().cancelAll();
    }
}
