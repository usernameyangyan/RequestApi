package com.request.api.async

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.request.api.builder.CreateRequestBuilderFactory
import com.request.api.common.ConvertType
import com.request.api.common.HttpReqModelType
import com.request.api.common.HttpReqParamType
import com.request.api.convert.RequestGsonConvert
import com.request.api.download.DownloadFileHelper
import com.request.api.download.DownloadInfo
import com.request.api.exception.BaseException
import com.request.api.exception.Description
import com.request.api.exception.OtherException
import com.request.api.exception.RequestExceptionCode
import com.request.api.manager.RetrofitManager
import com.request.api.rx.RxSchedulers
import com.request.api.rx.RxSubscriber
import com.request.api.service.RequestService
import com.request.api.utils.RequestFileUtils
import com.request.api.utils.RequestNetUtils
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody

/**
Create by yangyan
Create time:2023/9/6 11:56
Describe:
 */
class RequestMethod : RequestMethodImpl {
    private val mainHandler: Handler = Handler(Looper.getMainLooper())
    fun <T> request(builder: CreateRequestBuilderFactory.BaseRequestBuilder<T>): DisposableObserver<T>? {
        if (builder.getUrl() == null) {
            throw OtherException("RequestBuilder url not set!")
        }
         if(builder is CreateRequestBuilderFactory.RequestBuilder){
            val observable = getRetrofit(builder) ?: throw OtherException("Create observable failed!")
            return when (builder.getReqModelType()) {
                HttpReqModelType.NO_CACHE_OBJECT,HttpReqModelType.DEFAULT_CACHE_OBJECT->{
                    requestByNetWork(builder,observable,ConvertType.OBJECT)
                }
                HttpReqModelType.NO_CACHE_LIST,HttpReqModelType.DEFAULT_CACHE_LIST->{
                    requestByNetWork(builder,observable,ConvertType.LIST)
                }
                HttpReqModelType.DISK_CACHE_LIST_LIMIT_TIME->{
                    requestByDiskResultLimitTime(builder,observable,ConvertType.LIST)
                }
                HttpReqModelType.DISK_CACHE_OBJECT_LIMIT_TIME->{
                    requestByDiskResultLimitTime(builder,observable,ConvertType.LIST)
                }
                HttpReqModelType.DISK_CACHE_WITHOUT_NET_LIST->{
                    requestNoNetWorkByCacheResult(builder,observable,ConvertType.LIST)
                }
                HttpReqModelType.DISK_CACHE_WITHOUT_NET_OBJECT->{
                    requestNoNetWorkByCacheResult(builder,observable,ConvertType.OBJECT)
                }
                else->{
                    null
                }
            }
        }
        return null
    }

    private fun <T> getRetrofit(builder: CreateRequestBuilderFactory.RequestBuilder<T>): Observable<ResponseBody>? {
        val api = RetrofitManager.instance.getApiService(RequestService::class.java, builder)
        return when (builder.getReqParamType()) {
            HttpReqParamType.DEFAULT_GET -> {
                api.getReqByMapGet(
                    builder.getUrl(),
                    builder.getRequestParam(),
                    builder.getHeaders()
                )
            }

            HttpReqParamType.DEFAULT_POST -> {
                api.getReqByMapPost(
                    builder.getUrl(),
                    builder.getRequestParam(),
                    builder.getHeaders()
                )
            }

            HttpReqParamType.DEFAULT_DELETE -> {
                api.getReqByDelete(
                    builder.getUrl(),
                    builder.getHeaders()
                )
            }

            HttpReqParamType.MULTI_PART_POST -> {
                api.uploadFileByPost(
                    builder.getUrl(),
                    builder.getRequestParam(),
                    builder.getUploadFileParts(),
                    builder.getHeaders()
                )
            }

            HttpReqParamType.MULTI_PART_PUT -> {
                api.uploadFileByPut(
                    builder.getUrl(),
                    builder.getRequestParam(),
                    builder.getUploadFileParts(),
                    builder.getHeaders()
                )
            }

            HttpReqParamType.JSON_POST -> {
                val body = RequestGsonConvert.convertParamToJson(builder)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                api.getReqByJsonPost(
                    builder.getUrl(),
                    body,
                    builder.getHeaders()
                )
            }

            HttpReqParamType.BODY_POST -> {
                api.getReqByJsonPost(
                    builder.getUrl(),
                    builder.getBody(),
                    builder.getHeaders()
                )
            }

            HttpReqParamType.JSON_PUT -> {
                val body = RequestGsonConvert.convertParamToJson(builder)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                api.getReqByJsonByPut(
                    builder.getUrl(),
                    body,
                    builder.getHeaders()
                )
            }

            HttpReqParamType.BODY_PUT -> {
                api.getReqByJsonByPut(
                    builder.getUrl(),
                    builder.getBody(),
                    builder.getHeaders()
                )
            }

            HttpReqParamType.JSON_PATCH -> {
                val body = RequestGsonConvert.convertParamToJson(builder)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                api.getReqByJsonByPatch(
                    builder.getUrl(),
                    body,
                    builder.getHeaders()
                )
            }

            HttpReqParamType.BODY_PATCH -> {
                api.getReqByJsonByPatch(
                    builder.getUrl(),
                    builder.getBody(),
                    builder.getHeaders()
                )
            }

            HttpReqParamType.JSON_DELETE -> {
                val body = RequestGsonConvert.convertParamToJson(builder)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                api.getReqByJsonDelete(
                    builder.getUrl(),
                    body,
                    builder.getHeaders()
                )
            }

            HttpReqParamType.BODY_DELETE -> {
                api.getReqByJsonDelete(
                    builder.getUrl(),
                    builder.getBody(),
                    builder.getHeaders()
                )
            }

            else -> {
                null
            }
        }
    }

    override fun <T> downloadFile(builder: CreateRequestBuilderFactory.DownloadRequestBuilder): DisposableObserver<T> {
        if (builder.getDownloadFilePath() == null) {
            throw OtherException("RequestBuilder downloadFilePath not set!")
        }

        if (builder.getDownloadFileName() == null) {
            throw OtherException("RequestBuilder downloadFileName not set!")
        }
        return Observable.just(builder.getUrl()!!)
            .flatMap { Observable.just(DownloadFileHelper.createDownInfo(builder.getUrl()!!)) }
            .map { t ->
                DownloadFileHelper.getRealFileName(
                    t,
                    builder.getDownloadFilePath()!!,
                    builder.getDownloadFileName()!!,
                    builder.getIsOpenBreakpointDownload()
                )
            }.flatMap { downInfo ->
                RetrofitManager.instance.getApiService(RequestService::class.java, builder)
                    .downloadFile(
                        "bytes=" +
                                downInfo.progress + "-",
                        downInfo.url
                    )
            }.map {
                DownloadFileHelper
                    .writeCache(
                        it,
                        builder.getUrl()!!,
                        builder.getDownloadFilePath()!!,
                        builder.getDownloadFileName()!!
                    ) as T

            }.compose(RxSchedulers.threadToMain())
            .subscribeWith(object : RxSubscriber<T>() {
                override fun _onNext(t: T) {
                    val downloadInfo=t as DownloadInfo
                    if (!downloadInfo.isFinish && !RequestNetUtils.isNetworkConnected()) {
                        builder.getRequestNetListener().onError(
                            BaseException(
                                RequestExceptionCode.NETWORK_ERROR,
                                Description(-1, "Please check the network")
                            )
                        )
                    } else if (!downloadInfo.isFinish) {
                        builder.getRequestNetListener().onError(
                            BaseException(
                                RequestExceptionCode.UNKNOWN_ERROR,
                                Description(-1, "Download interrupted, please check if there is any abnormality in the downloaded file.")
                            )
                        )
                    } else {
                        builder.getRequestNetListener().onNext(downloadInfo)
                    }
                }

                override fun _onError(e: BaseException) {
                    builder.getRequestNetListener().onError(e)
                }
            })
    }

    @SuppressLint("CheckResult")
    override fun <T> requestByDiskResultLimitTime(
        builder: CreateRequestBuilderFactory.RequestBuilder<T>,
        observable: Observable<ResponseBody>,
        convertType: Int
    ): DisposableObserver<T>? {
        if (builder.getCacheFilePath() == null) {
            throw OtherException("RequestBuilder cacheFilePath not set!")
        }

        if (builder.getCacheFileName() == null) {
            throw OtherException("RequestBuilder cacheFileName not set!")
        }

        return if (!RequestFileUtils.isCacheDataFailure(
                builder.getCacheFilePath() + "/" + builder.getCacheFilePath(),
                builder.getCacheLimitHours()
            )
        ) {
            Observable.create<T> { emitter ->
                val json: String =
                    RequestFileUtils.readTxtFile(builder.getCacheFilePath() + "/" + builder.getCacheFileName())
                emitter.onNext(RequestGsonConvert.json2Result(builder, json, convertType))
            }.compose(RxSchedulers.threadToMain()).subscribeWith(object : RxSubscriber<T>() {
                override fun _onNext(t: T) {
                    builder.getRequestNetListener().onNext(t)
                }

                override fun _onError(e: BaseException) {
                    builder.getRequestNetListener().onError(e)
                }
            })
        } else {
            observable.map<T> { responseBody ->
                val s = responseBody.string()
                RequestFileUtils.writerTxtFile(
                    builder.getCacheFilePath()!!,
                    builder.getCacheFileName()!!,
                    s
                )
                RequestGsonConvert.json2Result(builder, s, convertType)
            }
                .compose(RxSchedulers.threadToMain())
                .subscribeWith(object : RxSubscriber<T>() {
                    override fun _onNext(t: T) {
                        builder.getRequestNetListener().onNext(t)
                    }

                    override fun _onError(e: BaseException) {
                        builder.getRequestNetListener().onError(e)
                    }
                })
        }
    }

    override fun <T> requestNoNetWorkByCacheResult(
        builder: CreateRequestBuilderFactory.RequestBuilder<T>,
        observable: Observable<ResponseBody>,
        convertType: Int
    ): DisposableObserver<T>? {
        if (builder.getCacheFilePath() == null) {
            throw OtherException("RequestBuilder cacheFilePath not set!")
        }

        if (builder.getCacheFileName() == null) {
            throw OtherException("RequestBuilder cacheFileName not set!")
        }
        return observable
            .map<T> { responseBody ->
                val s = responseBody.string()
                RequestFileUtils.writerTxtFile(
                    builder.getCacheFilePath()!!,
                    builder.getCacheFileName()!!,
                    s
                )
                RequestGsonConvert.json2Result(builder, s, convertType)
            }
            .compose(RxSchedulers.threadToMain())
            .subscribeWith(object : RxSubscriber<T>() {
                override fun _onNext(t: T) {
                    builder.getRequestNetListener().onNext(t)
                }

                @SuppressLint("CheckResult")
                override fun _onError(e: BaseException) {
                    Observable.create<T> { emitter ->
                        val json: String =
                            RequestFileUtils.readTxtFile(builder.getCacheFilePath()!! + "/" + builder.getCacheFileName()!!)
                        if (!TextUtils.isEmpty(json)) {
                            emitter.onNext(
                                RequestGsonConvert.json2Result(
                                    builder,
                                    json,
                                    convertType
                                )
                            )
                        } else {

                            mainHandler.post {
                                builder.getRequestNetListener().onError(e)
                            }
                        }
                    }.compose(RxSchedulers.threadToMain())
                        .subscribe { t -> builder.getRequestNetListener().onNext(t) }
                }
            })
    }

    override fun <T> requestByNetWork(
        builder: CreateRequestBuilderFactory.RequestBuilder<T>,
        observable: Observable<ResponseBody>,
        convertType: Int
    ): DisposableObserver<T>? {
        return observable.map<T> {
            RequestGsonConvert.body2Result(builder, it, convertType)
        }.compose(RxSchedulers.threadToMain()).subscribeWith(object : RxSubscriber<T>() {
            override fun _onNext(t: T) {
                builder.getRequestNetListener().onNext(t)
            }

            override fun _onError(e: BaseException) {
                builder.getRequestNetListener().onError(e)
            }
        })
    }
}