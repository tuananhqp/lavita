package com.vn3dart.lavita;

import android.app.Application;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Annv on 11/10/17.
 */

public class App extends Application {
    private static OkHttpClient client;

    public static OkHttpClient getHttpClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS).build();
        }
        return client;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getHttpClient();
    }
}
