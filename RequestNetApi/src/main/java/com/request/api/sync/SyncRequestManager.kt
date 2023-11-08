package com.request.api.sync

import com.request.api.builder.CreateRequestBuilderFactory
import com.request.api.common.ConvertType
import com.request.api.common.HttpReqModelType
import com.request.api.common.HttpReqParamType
import com.request.api.config.ReqConfig
import com.request.api.convert.RequestGsonConvert
import com.request.api.exception.BaseException
import com.request.api.exception.Description
import com.request.api.exception.OtherException
import com.request.api.exception.RequestExceptionCode
import com.request.api.manager.RetrofitManager
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
/**
Create by yangyan
Create time:2023/9/6 10:00
Describe:
 */
class SyncRequestManager {
    companion object {
        val instance: SyncRequestManager by lazy { SyncRequestManager() }
    }

    fun <T> request(requestBuilder: CreateRequestBuilderFactory.RequestBuilder<T>) {
        if (ReqConfig.instance == null) {
            throw OtherException("ReqConfig is not init!")
        }
        if (ReqConfig.instance!!.getUrlDomain() == null) {
            throw OtherException("ReqConfig is not init urlDomain!")
        }
        if (requestBuilder.getUrl() == null) {
            throw OtherException("request is not url")
        }

        try {
            val okHttpClient: OkHttpClient
            val builder = Request.Builder()
            if ((requestBuilder.getReqParamType() == HttpReqParamType.DEFAULT_GET ||
                        requestBuilder.getReqParamType() == HttpReqParamType.DEFAULT_POST ||
                        requestBuilder.getReqParamType() == HttpReqParamType.JSON_POST ||
                        requestBuilder.getReqParamType() == HttpReqParamType.MULTI_PART_POST ||
                        requestBuilder.getReqParamType() == HttpReqParamType.MULTI_PART_PUT ||
                        requestBuilder.getReqParamType() == HttpReqParamType.JSON_PUT ||
                        requestBuilder.getReqParamType() == HttpReqParamType.JSON_PATCH ||
                        requestBuilder.getReqParamType() == HttpReqParamType.JSON_DELETE) &&
                (requestBuilder.getReqModelType() == HttpReqModelType.NO_CACHE_OBJECT ||
                        requestBuilder.getReqModelType() == HttpReqModelType.NO_CACHE_LIST ||
                        requestBuilder.getReqModelType() == HttpReqModelType.DEFAULT_CACHE_OBJECT ||
                        requestBuilder.getReqModelType() == HttpReqModelType.DEFAULT_CACHE_LIST)
            ) {
                okHttpClient = RetrofitManager.instance.getOkHttpClient(requestBuilder)
                val url =
                    if (requestBuilder.getUrl()!!.contains("https://") || requestBuilder.getUrl()!!
                            .contains("http://")
                    ) {
                        requestBuilder.getUrl()
                    } else {
                        ReqConfig.instance!!.getUrlDomain() + requestBuilder.getUrl()
                    }
                if (requestBuilder.getReqParamType() == HttpReqParamType.DEFAULT_GET) {
                    val urlBuilder = url?.let { it.toHttpUrlOrNull()?.newBuilder() }
                    for ((key, value) in requestBuilder.getRequestParam()) {
                        urlBuilder?.addQueryParameter(key, value.toString())
                    }
                    for ((key, value) in requestBuilder.getHeaders()) {
                        builder.addHeader(key, value)
                    }
                    urlBuilder?.build()?.let { builder.url(it).get() }
                } else if (requestBuilder.getReqParamType() == HttpReqParamType.DEFAULT_POST) {
                    val requestBody = FormBody.Builder()
                    for ((key, value) in requestBuilder.getRequestParam()) {
                        requestBody.add(key, value.toString())
                    }
                    for ((key, value) in requestBuilder.getHeaders()) {
                        builder.addHeader(key, value)
                    }
                    builder.post(requestBody.build())
                } else if (requestBuilder.getReqParamType() == HttpReqParamType.MULTI_PART_POST ||
                    requestBuilder.getReqParamType() == HttpReqParamType.MULTI_PART_PUT
                ) {
                    //上传图片需要 MultipartBody
                    val body = MultipartBody.Builder().setType(MultipartBody.FORM)
                    requestBuilder.getUploadFileParts()?.map {
                        body.addPart(it)
                    }

                    for ((key, value) in requestBuilder.getRequestParam()) {
                        body.addFormDataPart(key, value.toString())
                    }
                    for ((key, value) in requestBuilder.getHeaders()) {
                        builder.addHeader(key, value)
                    }
                    val body1: RequestBody = body.build()
                    if (url != null) {
                        builder.url(url).post(body1)
                    }
                } else {
                    val data = if (requestBuilder.getReqParamWithoutKey() != null) {
                        requestBuilder.getReqParamWithoutKey().toString()
                    } else {
                        RequestGsonConvert.getGson().toJson(requestBuilder.getRequestParam())
                    }
                    val requestBody =
                        data.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                    if (url != null) {
                        builder.url(url).post(requestBody)
                    }
                }
                for ((key, value) in requestBuilder.getHeaders()) {
                    builder.addHeader(key, value)
                }
                val okRequest: Request = builder.build()
                val response = okHttpClient.newCall(okRequest).execute()
                if (response.code == 200) {
                    val convertType = when (requestBuilder.getReqModelType()) {
                        HttpReqModelType.NO_CACHE_OBJECT, HttpReqModelType.DEFAULT_CACHE_OBJECT -> {
                            ConvertType.OBJECT
                        }

                        else -> {
                            ConvertType.LIST
                        }
                    }
                    val a =
                        RequestGsonConvert.body2Result(requestBuilder, response.body!!, convertType)
                    requestBuilder.getRequestNetListener().onNext(a)
                } else {
                    requestBuilder.getRequestNetListener().onError(
                        BaseException(
                            RequestExceptionCode.RESPONSE_ERROR,
                            Description(response.code, response.message)
                        )
                    )
                }
            } else {
                throw OtherException("Synchronous requests are not supported yet:${requestBuilder.getReqParamType()}")
            }
        } catch (e: IOException) {
            requestBuilder.getRequestNetListener().onError(
                BaseException(
                    RequestExceptionCode.RESPONSE_ERROR,
                    Description(-1, e.message)
                )
            )
        }
    }
}