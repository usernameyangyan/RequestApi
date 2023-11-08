package com.request.api.cache

import com.request.api.config.ReqConfig
import com.request.api.utils.RequestNetUtils
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
Create by yangyan
Create time:2023/9/6 09:53
Describe:
 */
object DefaultCacheInterceptor {
    /**
     * ===============================Retrofit+OkHttp的缓存机制=========================================
     */
    fun getInterceptor(): Interceptor {
        return object : Interceptor {
            @Throws(java.io.IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val cacheBuilder = CacheControl.Builder()
                cacheBuilder.maxAge(0, TimeUnit.SECONDS)
                cacheBuilder.maxStale(365, TimeUnit.DAYS)
                val cacheControl = cacheBuilder.build()
                val request: Request =if(!RequestNetUtils.isNetworkConnected()){
                    chain.request().newBuilder()
                        .cacheControl(cacheControl)
                        .build()
                }else{
                    chain.request()
                }
                val originalResponse = chain.proceed(request)
                return if (RequestNetUtils.isNetworkConnected()) {
                    val maxAge = 0 // read from cache
                    originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public ,max-age=$maxAge")
                        .build()
                } else {
                    val maxStale = ReqConfig.instance!!.getMaxCacheSeconds() // tolerate 4-weeks stale
                    originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                        .build()
                }
            }
        }
    }
}