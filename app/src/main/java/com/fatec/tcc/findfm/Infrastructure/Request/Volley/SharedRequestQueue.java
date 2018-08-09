package com.fatec.tcc.findfm.Infrastructure.Request.Volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

public class SharedRequestQueue
{
    private static RequestQueue requestQueue;
    //Copiado do https://developer.android.com/training/volley/requestqueue
    private static SharedRequestQueue mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public static RequestQueue getRequestQueue(Context context) {
        if(requestQueue == null) {
            // Instantiate the cache
            Cache cache = new DiskBasedCache(context.getCacheDir(), (1024 * 1024) * 5); // 5MB cap
            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());
            // Create the Request Queue
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();
        }
        return requestQueue;
    }

    //Copiado do https://developer.android.com/training/volley/requestqueue
    private SharedRequestQueue(Context context)
    {
        mRequestQueue = getRequestQueue(context);

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized SharedRequestQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedRequestQueue(context);
        }
        return mInstance;
    }

    public static <T> void addToRequestQueue(Context context, Request<T> req) {
        getRequestQueue(context).add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}