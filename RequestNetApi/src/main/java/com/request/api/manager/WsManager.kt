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
class WsManager (wsRequestManager: WsRequestBuilder){

    private lateinit var request: Request
    private var pingTimer: Timer?=null
    private var connectTimer: Timer?=null
    private val wsRequestManager=wsRequestManager
    private var webSocket: WebSocket? = null
    private var isConnect: Boolean = false
    private var isResetConnect = false
    init {
        initWsConnect()
    }

    private fun initWsConnect(){
        webSocket?.cancel()
        request = Request.Builder()
            .url(wsRequestManager.getRequestUrl())
            .build()

        webSocket= wsRequestManager.getOkHttpClient().newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                wsRequestManager.getWsMessageListener().onMessage(text)
            }

            override fun onFailure(
                webSocket: WebSocket,
                t: Throwable,
                response: Response?
            ) {
                super.onFailure(webSocket, t, response)
                wsRequestManager.getWsMessageListener().onFailure(t)
                isResetConnect = true
                isConnect = false
                initData()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                isConnect = false
                wsRequestManager.getWsMessageListener().onClosed(code,reason)
                initData()
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                wsRequestManager.getWsMessageListener().onClosing(code,reason)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                wsRequestManager.getWsMessageListener().onMessage(bytes)
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                wsRequestManager.getWsMessageListener().onConnectSuccess()
                isConnect = true
                isResetConnect=false
                initParam()
            }

        })
    }

    private fun initParam(){
        if(wsRequestManager.getIsNeedResetConnect()){
            connectTimer?.cancel()
            connectTimer= Timer()
            connectTimer?.schedule(object :TimerTask(){
                override fun run() {
                    if(isResetConnect){
                        isResetConnect=false
                        initWsConnect()
                    }
                }

            },0,wsRequestManager.getResetConnectTime() )
        }

        if(wsRequestManager.getIsSendPing()){
            pingTimer?.cancel()
            pingTimer= Timer()
            pingTimer?.schedule(object :TimerTask(){
                override fun run() {
                    sendMsg("ping")
                }

            },0,wsRequestManager.getPingTime() )
        }
    }

    private fun initData(){
        webSocket?.cancel()
        connectTimer?.cancel()
        pingTimer?.cancel()
        connectTimer=null
        pingTimer=null
        webSocket=null
        isResetConnect = false
    }

    fun getWebSocket():WebSocket?{
        return webSocket
    }

    @Synchronized
    fun sendMsg(str: String) {
        webSocket?.send(str)
    }

    fun disconnect() {
        initData()
    }

    fun isConnect(): Boolean {
        return isConnect
    }


}