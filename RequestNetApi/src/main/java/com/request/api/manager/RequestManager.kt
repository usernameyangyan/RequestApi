package com.request.api.manager

import com.request.api.async.RequestMethod
import com.request.api.builder.CreateRequestBuilderFactory
import com.request.api.common.HttpReqEnv
import com.request.api.exception.OtherException
import com.request.api.sync.SyncRequestManager
import io.reactivex.observers.DisposableObserver

/**
Create by yangyan
Create time:2023/9/6 09:57
Describe:
 */
class RequestManager {
    companion object {
        val instance: RequestManager by lazy { RequestManager() }
    }
    private val requestMethod=RequestMethod()
    fun <T> request(builder: CreateRequestBuilderFactory.BaseRequestBuilder<T>): DisposableObserver<T>?{
        if (builder.getHttpReqEnv() == HttpReqEnv.ASYNCHRONOUS) {
            return requestMethod.request(builder)
        } else {
            if(builder is CreateRequestBuilderFactory.DownloadRequestBuilder){
                throw OtherException("Synchronization does not support downloading")
            }else if(builder is CreateRequestBuilderFactory.RequestBuilder){
                try {
                    SyncRequestManager.instance.request(builder)
                } catch (_: Exception) {
                }
            }
        }
        return null
    }
}