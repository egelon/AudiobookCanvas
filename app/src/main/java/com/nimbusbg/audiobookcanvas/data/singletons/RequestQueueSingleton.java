package com.nimbusbg.audiobookcanvas.data.singletons;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

import java.io.File;

public class RequestQueueSingleton {
    private static RequestQueueSingleton instance;
    private static RequestQueue requestQueue;
    private static Context ctx;
    
    private static String cacheName;
    private static int numThreads;

    private RequestQueueSingleton(Context context)
    {
        ctx = context;
        cacheName = "volley";
        numThreads = 4;//Runtime.getRuntime().availableProcessors();
        requestQueue = getRequestQueue();
    }

    public static synchronized RequestQueueSingleton getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new RequestQueueSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue()
    {
        if (requestQueue == null)
        {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = new RequestQueue(new DiskBasedCache(new File(ctx.getCacheDir(), cacheName)), new BasicNetwork(new HurlStack()), numThreads);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req)
    {
        getRequestQueue().add(req);
    }
    
    public void startQueue()
    {
        getRequestQueue().start();
    }
    
    public void stopQueue()
    {
        getRequestQueue().stop();
    }

}
