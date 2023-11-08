package com.request.api.interceptor

import android.text.TextUtils
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException

/**
Create by yangyan
Create time:2023/9/5 17:51
Describe:
 */
class BasicParamsInterceptor: Interceptor {
    private var queryParamsMap= HashMap<String, String>() // 添加到 URL 末尾，Get Post 方法都使用
    private var paramsMap= HashMap<String, String>()  // 添加到公共参数到消息体，适用 Post 请求
    private var headerParamsMap= HashMap<String, String>()  // 公共 Headers 添加
    private var headerLinesList= ArrayList<String>() // 消息头 集合形式，一次添加一行

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request? = chain.request()
        val requestBuilder: Request.Builder = request!!.newBuilder()

        // process header params inject
        val headerBuilder: Headers.Builder = request.headers.newBuilder()
        // 以 Entry 添加消息头
        if (headerParamsMap.size > 0) {
            val iterator: Iterator<*> = headerParamsMap.entries.iterator()
            while (iterator.hasNext()) {
                val (key, value) = iterator.next() as Map.Entry<*, *>
                (key as String?)?.let { (value as String?)?.let { it1 ->
                    headerBuilder.add(it,
                        it1
                    )
                } }
            }
        }
        // 以 String 形式添加消息头
        if (headerLinesList.size > 0) {
            for (line in headerLinesList) {
                headerBuilder.add(line)
            }
        }
        requestBuilder.headers(headerBuilder.build())

        // process queryParams inject whatever it's GET or POST
        if (queryParamsMap.size > 0) {
            request = injectParamsIntoUrl(request.url.newBuilder(), requestBuilder, queryParamsMap)
        }

        // process post body inject
        if (paramsMap.size > 0) {
            if (canInjectIntoBody(request)) {
                val formBodyBuilder = FormBody.Builder()
                for ((key, value) in paramsMap) {
                    formBodyBuilder.add(key, value)
                }
                val formBody: RequestBody = formBodyBuilder.build()
                var postBodyString = bodyToString(
                    request!!.body
                )
                postBodyString += (if (postBodyString.isNotEmpty()) "&" else "") + bodyToString(
                    formBody
                )
                requestBuilder.post(
                    postBodyString
                        .toRequestBody("application/x-www-form-urlencoded;charset=UTF-8".toMediaTypeOrNull())
                )
            }
        }
        request = requestBuilder.build()
        return chain.proceed(request)
    }

    /**
     * 确认是否是 post 请求
     * @param request 发出的请求
     * @return true 需要注入公共参数
     */
    private fun canInjectIntoBody(request: Request?): Boolean {
        if (request == null) {
            return false
        }
        if (!TextUtils.equals(request.method, "POST")) {
            return false
        }
        val body = request.body ?: return false
        val mediaType = body.contentType() ?: return false
        return TextUtils.equals(mediaType.subtype, "x-www-form-urlencoded")
    }

    // func to inject params into url
    private fun injectParamsIntoUrl(
        httpUrlBuilder: HttpUrl.Builder,
        requestBuilder: Request.Builder,
        paramsMap: Map<String, String>
    ): Request? {
        if (paramsMap.isNotEmpty()) {
            val iterator: Iterator<*> = paramsMap.entries.iterator()
            while (iterator.hasNext()) {
                val (key, value) = iterator.next() as Map.Entry<*, *>
                (key as String?)?.let { httpUrlBuilder.addQueryParameter(it, value as String?) }
            }
            requestBuilder.url(httpUrlBuilder.build())
            return requestBuilder.build()
        }
        return null
    }

    class Builder {
        private var interceptor: BasicParamsInterceptor = BasicParamsInterceptor()

        // 添加公共参数到 post 消息体
        fun addParam(key: String, value: String): Builder {
            interceptor.paramsMap[key] = value
            return this
        }

        // 添加公共参数到 post 消息体
        fun addParamsMap(paramsMap: Map<String, String>?): Builder {
            interceptor.paramsMap.putAll(paramsMap!!)
            return this
        }

        // 添加公共参数到消息头
        fun addHeaderParam(key: String, value: String): Builder {
            interceptor.headerParamsMap[key] = value
            return this
        }

        // 添加公共参数到消息头
        fun addHeaderParamsMap(headerParamsMap: Map<String, String>?): Builder {
            interceptor.headerParamsMap.putAll(headerParamsMap!!)
            return this
        }

        // 添加公共参数到消息头
        fun addHeaderLine(headerLine: String): Builder {
            val index = headerLine.indexOf(":")
            require(index != -1) { "Unexpected header: $headerLine" }
            interceptor.headerLinesList.add(headerLine)
            return this
        }

        // 添加公共参数到消息头
        fun addHeaderLinesList(headerLinesList: List<String>): Builder {
            for (headerLine in headerLinesList) {
                val index = headerLine.indexOf(":")
                require(index != -1) { "Unexpected header: $headerLine" }
                interceptor.headerLinesList.add(headerLine)
            }
            return this
        }

        // 添加公共参数到 URL
        fun addQueryParam(key: String, value: String): Builder {
            interceptor.queryParamsMap[key] = value
            return this
        }

        // 添加公共参数到 URL
        fun addQueryParamsMap(queryParamsMap: Map<String, String>?): Builder {
            interceptor.queryParamsMap.putAll(queryParamsMap!!)
            return this
        }

        fun build(): BasicParamsInterceptor {
            return interceptor
        }
    }

    companion object {
        private fun bodyToString(request: RequestBody?): String {
            return try {
                val buffer = Buffer()
                if (request != null) request.writeTo(buffer) else return ""
                buffer.readUtf8()
            } catch (e: IOException) {
                "did not work"
            }
        }
    }
}
