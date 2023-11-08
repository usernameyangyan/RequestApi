package com.request.api.config
import android.app.Application
import android.content.Context

/**
Create by yangyan
Create time:2023/9/5 17:09
Describe:
 */
class ReqConfig private constructor(private val context: Application) {
    companion object {
        var instance: ReqConfig? = null
        private fun createInstance(context: Application): ReqConfig {
            if (instance == null) {
                instance = ReqConfig(context)
            }
            return instance!!
        }

        fun init(context: Application): ReqConfig {
            return createInstance(context)
        }
    }

    //网络请求的域名
    private var urlDomain: String? = null

    //网络缓存地址
    private var reqCachePath: String? = null

    //设置OkHttp的缓存机制的最大缓存时间,默认为一天
    private var maxCacheSeconds = (60 * 60 * 24).toLong()

    //缓存最大的内存,默认为10M
    private var maxMemorySize = (10 * 1024 * 1024).toLong()

    //设置网络请求json通用解析类
    private var commonCovertClass: Class<*>? = null

    //过滤字段
    private var jsonFieldNullWillHide: String? = null

    /***请求接口超时设定 */
    private var connectTimeoutSeconds = 60L
    private var readTimeoutSeconds = 60L
    private var writeTimeoutSeconds = 60L
    private var isShowLog = false

    /***设置全局请求头 */
    private var headers: HashMap<String, String>? = null

    fun setUrlDomain(urlDomain: String): ReqConfig {
        this.urlDomain = urlDomain
        return this
    }

    fun getUrlDomain(): String? {
        return urlDomain
    }

    fun setReqCachePath(reqCachePath: String): ReqConfig {
        this.reqCachePath = reqCachePath
        return this
    }

    fun getReqCachePath(): String? {
        return reqCachePath
    }

    fun setMaxCacheSeconds(maxCacheSeconds: Long): ReqConfig {
        this.maxCacheSeconds = maxCacheSeconds
        return this
    }

    fun getMaxCacheSeconds(): Long {
        return maxCacheSeconds
    }

    fun setMaxMemorySize(maxMemorySize: Long): ReqConfig {
        this.maxMemorySize = maxMemorySize
        return this
    }

    fun getMaxMemorySize(): Long {
        return maxMemorySize
    }

    fun setCommonCovertClass(commonCovertClass: Class<*>): ReqConfig {
        this.commonCovertClass = commonCovertClass
        return this
    }

    fun getCommonCovertClass(): Class<*>? {
        return commonCovertClass
    }

    fun setJsonFieldNullWillHide(jsonFieldNullWillHide: String): ReqConfig {
        this.jsonFieldNullWillHide = jsonFieldNullWillHide
        return this
    }

    fun getJsonFieldNullWillHide(): String? {
        return jsonFieldNullWillHide
    }

    fun setConnectTimeoutSeconds(connectTimeoutSeconds: Long): ReqConfig {
        this.connectTimeoutSeconds = connectTimeoutSeconds
        return this
    }

    fun getConnectTimeoutSeconds(): Long {
        return connectTimeoutSeconds
    }

    fun setReadTimeoutSeconds(readTimeoutSeconds: Long): ReqConfig {
        this.readTimeoutSeconds = readTimeoutSeconds
        return this
    }

    fun getReadTimeoutSeconds(): Long {
        return readTimeoutSeconds
    }

    fun setWriteTimeoutSeconds(writeTimeoutSeconds: Long): ReqConfig {
        this.writeTimeoutSeconds = writeTimeoutSeconds
        return this
    }

    fun getWriteTimeoutSeconds(): Long {
        return writeTimeoutSeconds
    }

    fun setHeaders(headers: HashMap<String, String>): ReqConfig {
        this.headers = headers
        return this
    }

    fun getHeaders(): HashMap<String, String>? {
        return headers
    }

    fun setShowLog(isShowLog: Boolean): ReqConfig {
        this.isShowLog = isShowLog
        return this
    }

    fun getShowLog(): Boolean {
        return isShowLog
    }
    fun getContext():Context{
        return context
    }
}