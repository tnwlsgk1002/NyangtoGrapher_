package kr.co.nyangtographer.android.nyangtographer

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface APIservice {
    @Multipart
    @POST("/predict")
    fun getRes(
        @Part ("AnsId") AnsId: RequestBody,
        @Part file: MultipartBody.Part?
    ): Call<SIM>
}