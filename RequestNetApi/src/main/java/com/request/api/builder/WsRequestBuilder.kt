package com.request.api.builder

import com.request.api.impl.IWsMessageListener
import okhttp3.OkHttpClient

/**
Create by yangyan
Create time:2023/11/14 16:33
Describe:
 */
class WsRequestBuilder(wsMessageListener: IWsMessageListener) {
    private var pingTime:Long=1000L
    private var isSendPing=true
    private var mClient: OkHttpClient
    private var requestUrl:String=""
    private var resetConnectTime:Long=5000L
    private var isNeedResetConnect=true
    private var wsMessageListener=wsMessageListener
    init {
        mClient = OkHttpClient.Builder()
            .build()
    }

    fun getWsMessageListener():IWsMessageListener{
        return wsMessageListener
    }
    fun setPingTime(pingTime:Long):WsRequestBuilder{
        this.pingTime=pingTime
        return this
    }
    fun getPingTime():Long{
        return pingTime
    }
    fun setIsSendPing(isSendPing:Boolean):WsRequestBuilder{
        this.isSendPing=isSendPing
        return this
    }
    fun getIsSendPing():Boolean{
        return isSendPing
    }
    fun setOkHttpClient(mClient: OkHttpClient):WsRequestBuilder{
        this.mClient=mClient
        return this
    }
    fun getOkHttpClient():OkHttpClient{
        return mClient
    }
    fun setRequestUrl(requestUrl: String):WsRequestBuilder{
        this.requestUrl=requestUrl
        return this
    }
    fun getRequestUrl():String{
        return requestUrl
    }
    fun setResetConnectTime(resetConnectTime:Long):WsRequestBuilder{
        this.resetConnectTime=resetConnectTime
        return this
    }
    fun getResetConnectTime():Long{
        return resetConnectTime
    }

    fun setIsNeedResetConnect(isNeedResetConnect:Boolean):WsRequestBuilder{
        this.isNeedResetConnect=isNeedResetConnect
        return this
    }
    fun getIsNeedResetConnect():Boolean{
        return isNeedResetConnect
    }


}