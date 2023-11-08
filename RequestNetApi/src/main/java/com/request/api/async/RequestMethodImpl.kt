package com.request.api.async

import com.request.api.builder.CreateRequestBuilderFactory
import com.request.api.common.ConvertType
import com.request.api.download.DownloadInfo
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver
import okhttp3.ResponseBody

/**
Create by yangyan
Create time:2023/9/6 11:51
Describe:
 */
interface RequestMethodImpl {
    /**
     * 下载文件
     * @param builder
     * @return
     */
    fun <T> downloadFile(
        builder: CreateRequestBuilderFactory.DownloadRequestBuilder
    ): DisposableObserver<T>

    /**
     * 设置缓存时间，没超过设置的时间不请求网络，只返回缓存数据
     * @param builder
     * @param observable
     */
    fun <T> requestByDiskResultLimitTime(
        builder: CreateRequestBuilderFactory.RequestBuilder<T>,
        observable: Observable<ResponseBody>,
        @ConvertType convertType: Int
    ): DisposableObserver<T>?



    /**
     * 没有网络再请求缓存
     * @param builder
     * @param observable
     */
    fun <T> requestNoNetWorkByCacheResult(
        builder: CreateRequestBuilderFactory.RequestBuilder<T>,
        observable: Observable<ResponseBody>,
        @ConvertType convertType: Int
    ):DisposableObserver<T>?


    /**
     * 把只通过网络返回数据
     * @param builder
     * @param observable
     */
    fun <T> requestByNetWork(
        builder: CreateRequestBuilderFactory.RequestBuilder<T>,
        observable: Observable<ResponseBody>,
        @ConvertType convertType: Int
    ): DisposableObserver<T>?
}