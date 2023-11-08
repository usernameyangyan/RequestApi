package com.request.api.service

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.QueryMap
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
Create by yangyan
Create time:2023/9/5 16:58
Describe:
 */
interface RequestService {
    @GET
    @Streaming
    fun downloadFile(@Header("Range") start: String?, @Url url: String?): Observable<ResponseBody>?

    @Multipart
    @POST
    fun uploadFileByPost(
        @Url url: String?, @QueryMap map: HashMap<String, Any>?,
        @Part images: Array<MultipartBody.Part>?, @HeaderMap headers: HashMap<String, String>?
    ): Observable<ResponseBody>?


    @Multipart
    @PUT
    fun uploadFileByPut(
        @Url url: String?, @QueryMap map: HashMap<String, Any>?,
        @Part images:Array<MultipartBody.Part>?,
        @HeaderMap headers: HashMap<String, String>?
    ): Observable<ResponseBody>?


    @GET
    fun getReqByMapGet(
        @Url url: String?,
        @QueryMap map: HashMap<String, Any>?,
        @HeaderMap header: Map<String, String>?
    ): Observable<ResponseBody>?

    @HTTP(method = "DELETE", hasBody = true)
    fun getReqByDelete(@Url url: String?, @HeaderMap headers: HashMap<String, String>?): Observable<ResponseBody>?

    @FormUrlEncoded
    @POST
    fun getReqByMapPost(
        @Url url: String?,
        @FieldMap map: HashMap<String, Any>?,
        @HeaderMap headers: HashMap<String, String>?
    ): Observable<ResponseBody>?



    @POST
    fun getReqByJsonPost(
        @Url url: String?,
        @Body json: RequestBody?,
        @HeaderMap headers: HashMap<String, String>?
    ): Observable<ResponseBody>?


    @PUT
    fun getReqByJsonByPut(
        @Url url: String?,
        @Body json: RequestBody?,
        @HeaderMap headers: HashMap<String, String>?
    ): Observable<ResponseBody>?

    @PATCH
    fun getReqByJsonByPatch(
        @Url url: String?,
        @Body json: RequestBody?,
        @HeaderMap headers: HashMap<String, String>?
    ): Observable<ResponseBody>?


    @HTTP(method = "DELETE", hasBody = true)
    fun getReqByJsonDelete(
        @Url url: String?,
        @Body json: RequestBody?,
        @HeaderMap headers: HashMap<String, String>?
    ): Observable<ResponseBody>?

}