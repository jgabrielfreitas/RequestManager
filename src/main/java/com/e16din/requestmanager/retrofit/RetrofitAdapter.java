package com.e16din.requestmanager.retrofit;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by e16din on 20.08.15.
 */
public class RetrofitAdapter extends BaseRetrofitAdapter {

    public static Object getService(Class requestManagerInterface, String endpoint, Map<String, String> headers) {
        return getService(requestManagerInterface, endpoint, headers, new GsonBuilder().create(), null);
    }

    public static Object getService(Class requestManagerInterface, String endpoint, Map<String, String> headers, Gson gson, Cache cache) {
        OkHttpClient client = new OkHttpClient();
        if (cache != null)
            client.setCache(cache);
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);

        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setErrorHandler(new StaticErrorHandler())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(getRequestInterceptor(headers))
                .setConverter(new GsonConverter(gson))
                .setClient(new OkClient(client))
                .build()

                .create(requestManagerInterface);
    }

    @NonNull
    private static RequestInterceptor getRequestInterceptor(final Map<String, String> headers) {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                for (String key : headers.keySet()) {
                    request.addHeader(key, headers.get(key));
                }

                if (!TextUtils.isEmpty(getCookies()))
                    request.addHeader("Cookie", getCookies());
            }
        };
    }
}
