package com.example.request.api.demo

import com.request.api.builder.CreateRequestBuilderFactory
import com.request.api.download.DownloadInfo
import com.request.api.exception.BaseException
import com.request.api.impl.IRequestNetListener
import com.request.api.manager.RequestManager

/**
Create by yangyan
Create time:2023/9/7 13:54
Describe:
 */
class MainActivity {
    init {
        val builder=CreateRequestBuilderFactory.createDownloadRequestBuilder(object :IRequestNetListener<DownloadInfo>{
            override fun onNext(result: DownloadInfo?) {
            }

            override fun onError(e: BaseException) {
            }

        })


        RequestManager.instance.request(builder)
    }
}