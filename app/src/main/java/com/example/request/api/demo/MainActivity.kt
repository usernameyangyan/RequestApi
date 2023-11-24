package com.example.request.api.demo

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
class MainActivity :AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    init {

        val url="ws://10.153.151.1:8000"
        val wsRequestBuilder=WsRequestBuilder(object :IWsMessageListener{
            override fun onMessage(message: String) {
                Log.d("100000","来到这里onMessage")
            }

            override fun onConnectSuccess() {
                Log.d("100000","来到这里onConnectSuccess")
            }

            override fun onFailure(t: Throwable) {
                Log.d("100000","来到这里onFailure："+t.message)
            }

            override fun onClosed(code: Int, reason: String) {
                Log.d("100000","来到这里onClosed")
            }

        })
        wsRequestBuilder
            .setIsSendPing(true)
            .setPingTime(1000)
            .setIsNeedResetConnect(true)
            .setResetConnectTime(5000)
            .setRequestUrl(url)
        RequestManager.instance.request(wsRequestBuilder)
//        val builder=CreateRequestBuilderFactory.createDownloadRequestBuilder(object :IRequestNetListener<DownloadInfo>{
//            override fun onNext(result: DownloadInfo?) {
//            }
//
//            override fun onError(e: BaseException) {
//            }
//
//        })
//
//        val builder1=WsRequestBuilder(object :IWsMessageListener{
//            override fun onMessage(message: String) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onConnectSuccess() {
//                TODO("Not yet implemented")
//            }
//
//            override fun onFailure(t: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onClosed(code: Int, reason: String) {
//                TODO("Not yet implemented")
//            }
//
//        })
    }
}