package com.kotdev.espress.network


import com.kotdev.espress.model.STKPushRequest
import com.kotdev.espress.model.STKPushResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Header

interface DarajaApi {
    @POST("mpesa/stkpush/v1/processrequest")
    fun pushSTK(
        @Header("Authorization") token: String,
        @Body request: STKPushRequest
    ): Call<STKPushResponse>
}
