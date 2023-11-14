package com.example.request.api.demo

import com.request.api.builder.CreateRequestBuilderFactory
import com.request.api.builder.WsRequestBuilder
import com.request.api.download.DownloadInfo
import com.request.api.exception.BaseException
import com.request.api.impl.IRequestNetListener
import com.request.api.impl.IWsMessageListener
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

        val builder1=WsRequestBuilder(object :IWsMessageListener{
            override fun onMessage(message: String) {
                TODO("Not yet implemented")
            }

            override fun onConnectSuccess() {
                TODO("Not yet implemented")
            }

            override fun onFailure(t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onClosed(code: Int, reason: String) {
                TODO("Not yet implemented")
            }

        })
    }
}