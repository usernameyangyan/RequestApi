package com.request.api.manager
import android.util.Log
import com.request.api.builder.CreateRequestBuilderFactory
import com.request.api.cache.DefaultCacheInterceptor
import com.request.api.common.HttpReqModelType
import com.request.api.common.HttpReqParamType
import com.request.api.config.ReqConfig
import com.request.api.download.DownloadProgressBody
import com.request.api.exception.OtherException
import com.request.api.interceptor.BasicParamsInterceptor
import com.request.api.upload.UploadProgressBody
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * Created by yangy
 *2020-02-20
 *Describe:
 */

class RetrofitManager private constructor(){
    companion object {
        val instance: RetrofitManager by lazy { RetrofitManager() }
    }
    private fun getRetrofit(requestBuilder: CreateRequestBuilderFactory.BaseRequestBuilder<*>): Retrofit {
        if (ReqConfig.instance == null) {
            throw OtherException("ReqConfig is not init!")
        }
        if (ReqConfig.instance!!.getUrlDomain() == null) {
            throw OtherException("ReqConfig is not init urlDomain!")
        }
        return Retrofit.Builder().baseUrl(ReqConfig.instance!!.getUrlDomain()!!)
            .client(getOkHttpClient(requestBuilder))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    fun getOkHttpClient(requestBuilder: CreateRequestBuilderFactory.BaseRequestBuilder<*>): OkHttpClient {
        if (ReqConfig.instance == null) {
            throw OtherException("ReqConfig is not init!")
        }
        if (ReqConfig.instance!!.getUrlDomain() == null) {
            throw OtherException("ReqConfig is not init urlDomain!")
        }
        val builder = OkHttpClient.Builder()
        //如果不是在正式包，添加拦截 打印响应json
        if (ReqConfig.instance!!.getShowLog()) {
            val logging = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Log.d("RetrofitManager", "收到响应: $message")
                }
            })

            if(requestBuilder is CreateRequestBuilderFactory.DownloadRequestBuilder){
                logging.setLevel(HttpLoggingInterceptor.Level.HEADERS)
            }else if(requestBuilder is CreateRequestBuilderFactory.RequestBuilder){
                if (requestBuilder.getReqParamType() == HttpReqParamType.MULTI_PART_POST
                ) {
                    logging.setLevel(HttpLoggingInterceptor.Level.HEADERS)
                } else {
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                }
            }
            builder.addInterceptor(logging)
        }

        if(requestBuilder is CreateRequestBuilderFactory.DownloadRequestBuilder){
            builder.addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val originalResponse: Response = chain.proceed(chain.request())
                    return originalResponse.newBuilder()
                        .body(
                            DownloadProgressBody(
                                originalResponse.body!!,
                                requestBuilder
                            )
                        )
                        .build()

                }
            })
        } else if(requestBuilder is CreateRequestBuilderFactory.RequestBuilder) {
            ReqConfig.instance?.getHeaders()?.let {
                val basicParamsInterceptor = BasicParamsInterceptor.Builder()
                    .addHeaderParamsMap(it)
                    .build()

                builder.addInterceptor(basicParamsInterceptor)
            }
            requestBuilder.getReqModelType()?.let {
                if (isCache(it) && ReqConfig.instance!!.getReqCachePath() != null) {
                    val httpCacheDirectory = File(ReqConfig.instance!!.getReqCachePath()!!)
                    builder.cache(
                        Cache(
                            httpCacheDirectory,
                            ReqConfig.instance!!.getMaxMemorySize()
                        )
                    )
                    builder.addInterceptor(DefaultCacheInterceptor.getInterceptor())
                }
            }

            if (requestBuilder.getReqModelType() == HttpReqParamType.MULTI_PART_POST) {
                builder.addInterceptor(object : Interceptor {
                    @Throws(IOException::class)
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val original = chain.request()

                        val request = original.newBuilder()
                            .method(
                                original.method,
                                UploadProgressBody(original.body!!, requestBuilder)
                            )
                            .build()

                        return chain.proceed(request)
                    }
                })
            }

        }

        return builder.connectTimeout(ReqConfig.instance!!.getConnectTimeoutSeconds(), TimeUnit.SECONDS)
            .readTimeout(ReqConfig.instance!!.getReadTimeoutSeconds(), TimeUnit.SECONDS)
            .writeTimeout(ReqConfig.instance!!.getWriteTimeoutSeconds(), TimeUnit.SECONDS)
            .build()
    }

    //是否需要设置Retrofit缓存
    private fun isCache(type: Int): Boolean {
        if (type == HttpReqModelType.DEFAULT_CACHE_LIST || type == HttpReqModelType.DEFAULT_CACHE_OBJECT
        ) {
            return true
        }
        return false
    }


    /**
     * 接口定义类
     * @param tClass
     * @param requestBuilder
     * @return
     */
    fun <T> getApiService(tClass: Class<T>, requestBuilder: CreateRequestBuilderFactory.BaseRequestBuilder<*>): T {
        return getRetrofit(requestBuilder).create(tClass)
    }

    fun getCustomizeRequest(): CustomizeRequest {
        return CustomizeRequest.instance
    }
    class CustomizeRequest private constructor(){
        companion object {
            val instance: CustomizeRequest by lazy { CustomizeRequest() }
        }
        private var okHttpClient: OkHttpClient? = null
        /**
         * 自定义
         */
        fun setCustomizeOkHttpClient(okHttpClient: OkHttpClient): CustomizeRequest {
            this.okHttpClient = okHttpClient
            return this
        }

        fun <T> getCustomizeApiService(tClass: Class<T>): T {
            return getRetrofit().create(tClass)
        }
        /**
         * 自定义默认Retrofit
         */
        private fun getRetrofit(): Retrofit {
            if (ReqConfig.instance == null) {
                throw OtherException("ReqConfig is not init!")
            }
            if (ReqConfig.instance!!.getUrlDomain() == null) {
                throw OtherException("ReqConfig is not init urlDomain!")
            }
            if (okHttpClient == null) {
                okHttpClient = getDefaultOkHttpClient()
            }
            return Retrofit.Builder().baseUrl(ReqConfig.instance!!.getUrlDomain()!!)
                .client(okHttpClient!!)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }


        /**
         * 自定义默认OkHttpClient
         */
        private fun getDefaultOkHttpClient(): OkHttpClient {
            if (ReqConfig.instance == null) {
                throw OtherException("ReqConfig is not init!")
            }

            val builder = OkHttpClient.Builder()
            //如果不是在正式包，添加拦截 打印响应json
            if (ReqConfig.instance!!.getShowLog()) {
                val logging = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                    override fun log(message: String) {
                        Log.d("RetrofitManager", "收到响应: $message")
                    }
                })

                logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                builder.addInterceptor(logging)

            }
            ReqConfig.instance!!.getHeaders()?.let {
                val basicParamsInterceptor = BasicParamsInterceptor.Builder()
                    .addHeaderParamsMap(it)
                    .build()

                builder.addInterceptor(basicParamsInterceptor)
            }

            ReqConfig.instance!!.getReqCachePath()?.let {
                //设置缓存
                val httpCacheDirectory = File(it)
                builder.cache(Cache(httpCacheDirectory, ReqConfig.instance!!.getMaxMemorySize()))
                builder.addInterceptor(DefaultCacheInterceptor.getInterceptor())
            }

            return builder.connectTimeout(
                ReqConfig.instance!!.getConnectTimeoutSeconds(),
                TimeUnit.SECONDS
            )
                .readTimeout(ReqConfig.instance!!.getReadTimeoutSeconds(), TimeUnit.SECONDS)
                .writeTimeout(ReqConfig.instance!!.getWriteTimeoutSeconds(), TimeUnit.SECONDS)
                .build()
        }
    }
}
