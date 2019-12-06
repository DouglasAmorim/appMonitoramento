package com.example.appmonitoramento;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by RenanTeles on 22/02/16.
 * Consumir Json no Android
 */
public class ServiceGenerator {
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


    //URL base do endpoint. Deve sempre terminar com /
    //public static final String API_BASE_URL = "https://community-neutrino-currency-conversion.p.mashape.com/";
    public static final String API_BASE_URL = "http://34.95.152.203/";

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit_t = builder.build();

    public static <S> S createService(Class<S> serviceClass, String username, String password) {

        //Instancia do interceptador das requisições
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        httpClient.readTimeout(15, TimeUnit.SECONDS);

        httpClient.addInterceptor(new BasicAuthInterceptor(username, password));


        //Instância do retrofit 
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(httpClient.build())
                .build();

        return retrofit.create(serviceClass);
    }

    public static <S> S createService(
            Class<S> serviceClass, final String authToken) {
        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptor =
                    new AuthenticationInterceptor(authToken);

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);

                builder.client(httpClient.build());
                retrofit_t = builder.build();
            }
        }

        return retrofit_t.create(serviceClass);
    }
}