package com.example.appmonitoramento;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RetrofitService {

    //@Headers("X-Mashape-Key: AuuyclCPjcmshv2iOPq190OpzLrMp1FJWwejsnJrdfwOUr4h44")
    @Headers("Content-Type: application/json")
    //@FormUrlEncoded
    @POST("consultar")
    Call<RespostaServidor> consultarConsumo(@Body RequestBody idUsuario);

}