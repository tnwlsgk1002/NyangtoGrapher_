package kr.co.nyangtographer.android.nyangtographer

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object APIserviceImplemention {
    private const val BASE_URL = "http://10.0.2.2:5000/"
    //private const val BASE_URL = "https://127.0.0.1:5000/"
    private val okHttpClient = OkHttpClient
            .Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val service: APIservice = retrofit.create(APIservice::class.java)
}