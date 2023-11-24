package com.request.api.manager
import com.request.api.builder.WsRequestBuilder
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.Timer
import java.util.TimerTask

/**
Create by yangyan
Create time:2023/11/14 17:24
Describe:
 */
class WsManager (wsRequestBuilder: WsRequestBuilder){

    private lateinit var request: Request
    private var pingTimer: Timer?=null
    private var connectTimer: Timer?=null
    private val wsRequestBuilder=wsRequestBuilder
    private var webSocket: WebSocket? = null
    private var isConnect: Boolean = false
    private var isResetConnect = false
    init {
        initWsConnect()
        if(wsRequestBuilder.getIsNeedResetConnect()){
            connectTimer?.cancel()
            connectTimer= Timer()
            connectTimer?.schedule(object :TimerTask(){
                override fun run() {
                    if(isResetConnect){
                        isResetConnect=false
                        initWsConnect()
                    }
                }

            },0,wsRequestBuilder.getResetConnectTime() )
        }
    }

    private fun initWsConnect(){
        webSocket?.cancel()
        request = Request.Builder()
            .url(wsRequestBuilder.getRequestUrl())
            .build()

        webSocket= wsRequestBuilder.getOkHttpClient().newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                wsRequestBuilder.getWsMessageListener().onMessage(text)
            }

            override fun onFailure(
                webSocket: WebSocket,
                t: Throwable,
                response: Response?
            ) {
                super.onFailure(webSocket, t, response)
                wsRequestBuilder.getWsMessageListener().onFailure(t)
                isResetConnect = true
                isConnect = false
                initData(wsRequestBuilder.getIsNeedResetConnect())
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                isConnect = false
                wsRequestBuilder.getWsMessageListener().onClosed(code,reason)
                initData(false)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                wsRequestBuilder.getWsMessageListener().onClosing(code,reason)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                wsRequestBuilder.getWsMessageListener().onMessage(bytes)
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                wsRequestBuilder.getWsMessageListener().onConnectSuccess()
                isConnect = true
                isResetConnect=false
                initParam()
            }

        })
    }

    private fun initParam(){
        if(wsRequestBuilder.getIsSendPing()){
            pingTimer?.cancel()
            pingTimer= Timer()
            pingTimer?.schedule(object :TimerTask(){
                override fun run() {
                    sendMsg("ping")
                }

            },0,wsRequestBuilder.getPingTime() )
        }
    }

    private fun initData(isNeedResetConnect:Boolean){
        if(!isNeedResetConnect){
            connectTimer?.cancel()
            connectTimer=null
            isResetConnect = false
        }
        webSocket?.cancel()
        pingTimer?.cancel()
        pingTimer=null
        webSocket=null

    }

    fun getWebSocket():WebSocket?{
        return webSocket
    }

    @Synchronized
    fun sendMsg(str: String) {
        webSocket?.send(str)
    }

    fun disconnect() {
        initData(false)
    }

    fun isConnect(): Boolean {
        return isConnect
    }
}