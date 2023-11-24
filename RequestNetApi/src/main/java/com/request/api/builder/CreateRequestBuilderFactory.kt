package com.request.api.builder

import com.request.api.common.HttpReqEnv
import com.request.api.common.HttpReqModelType
import com.request.api.common.HttpReqParamType
import com.request.api.download.DownloadInfo
import com.request.api.impl.IDownloadListener
import com.request.api.impl.IRequestNetListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
Create by yangyan
Create time:2023/9/7 10:14
Describe:
 */
class CreateRequestBuilderFactory {
    companion object {
        private val instance: CreateRequestBuilderFactory by lazy { CreateRequestBuilderFactory() }
        fun <T> createRequestBuilder(requestNetListener: IRequestNetListener<T>): RequestBuilder<T> {
            return instance.RequestBuilder(requestNetListener)
        }

        fun createDownloadRequestBuilder(requestNetListener: IDownloadListener<DownloadInfo>): DownloadRequestBuilder {
            return instance.DownloadRequestBuilder(requestNetListener)
        }
    }

    inner class RequestBuilder<T>(requestNetListener: IRequestNetListener<T>) :
        BaseRequestBuilder<T>(requestNetListener) {
        private var clazz: Class<*>? = null //转化Class
        private var requestParam = HashMap<String, Any>()
        private var headers = HashMap<String, String>()
        private var body: RequestBody? = null
        private var filePath: String? = null
        private var fileName: String? = null
        private var reqParamWithoutKey: Any? = null
        private var limitHours = 1
        private var reqParamType: Int? = null
        private var reqModelType: Int? = null
        private var parts: Array<MultipartBody.Part>? = null
        private var isReturnJson: Boolean = false
        private var isUseCommonClass = true

        init {
            headers["Connection"] = "close"
        }
        fun setConvertClass(clazz: Class<*>): RequestBuilder<*> {
            this.clazz = clazz
            return this
        }

        fun getConvertClass(): Class<*>? {
            return clazz
        }

        fun setBody(body: RequestBody): RequestBuilder<*> {
            this.body = body
            return this
        }

        fun getBody(): RequestBody? {
            return body
        }

        fun setCacheFilePathAndName(
            filePath: String,
            fileName: String
        ): RequestBuilder<*> {
            this.filePath = filePath
            this.fileName = fileName
            return this
        }

        fun getCacheFilePath(): String? {
            return filePath
        }

        fun getCacheFileName(): String? {
            return fileName
        }

        fun getCacheLimitHours(): Int {
            return limitHours
        }

        fun setCacheLimitHours(limitHours: Int) {
            this.limitHours = limitHours
        }

        fun setHeader(
            key: String,
            value: String
        ): RequestBuilder<*> {
            headers[key] = value
            return this
        }

        fun setHeaders(headers: HashMap<String, String>): RequestBuilder<*> {
            this.headers.putAll(headers)
            return this
        }

        fun getReqParamWithoutKey(): Any? {
            return reqParamWithoutKey
        }

        fun getHeaders(): HashMap<String, String> {
            return headers
        }

        fun setNoKeyParam(reqParamWithoutKey: Any): RequestBuilder<*> {
            this.reqParamWithoutKey = reqParamWithoutKey
            return this
        }

        fun setReqParam(key: String, value: Any): RequestBuilder<*> {
            requestParam[key] = value
            return this
        }

        fun setRequestParam(requestParam: HashMap<String, Any>): RequestBuilder<*> {
            this.requestParam.putAll(requestParam)
            return this
        }

        fun getRequestParam(): HashMap<String, Any> {
            return requestParam
        }

        fun setHttpTypeAndReqType(
            @HttpReqParamType reqParamType: Int,
            @HttpReqModelType reqModelType: Int
        ): RequestBuilder<*> {
            this.reqModelType = reqModelType
            this.reqParamType = reqParamType
            return this
        }

        fun getReqParamType(): Int? {
            return reqParamType
        }

        fun getReqModelType(): Int? {
            return reqModelType
        }

        fun setUploadFilePaths(
            key: String,
            filePaths: Array<String>
        ): RequestBuilder<*> {
            val parts = arrayOfNulls<MultipartBody.Part>(filePaths.size)
            for (i in filePaths.indices) {
                val file = File(filePaths[i])
                val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val part: MultipartBody.Part =
                    MultipartBody.Part.createFormData(key, file.name, requestBody)
                parts[i] = part
            }
            return this
        }

        fun getUploadFileParts(): Array<MultipartBody.Part>? {
            return parts
        }

        fun getIsReturnJson(): Boolean {
            return isReturnJson
        }

        fun setIsReturnJson(isReturnJson: Boolean): RequestBuilder<*> {
            this.isReturnJson = isReturnJson
            return this
        }

        fun isUseCommonConvertClass(): Boolean {
            return isUseCommonClass
        }

        fun setIsUseConvertCommonClass(useCommonClass: Boolean): RequestBuilder<*> {
            isUseCommonClass = useCommonClass
            return this
        }
    }

    inner class DownloadRequestBuilder(requestNetListener: IDownloadListener<DownloadInfo>) :
        BaseRequestBuilder<DownloadInfo>(requestNetListener) {
        private var saveDownFilePath: String? = null
        private var saveDownFileName: String? = null
        private var isOpenBreakpointDownload=true
        fun setDownloadFilePathAndName(
            filePath: String,
            fileName: String
        ): DownloadRequestBuilder {
            this.saveDownFilePath = filePath
            this.saveDownFileName = fileName
            return this
        }

        fun getDownloadFilePath(): String? {
            return saveDownFilePath
        }

        fun getDownloadFileName(): String? {
            return saveDownFileName
        }
        fun getIsOpenBreakpointDownload():Boolean{
            return isOpenBreakpointDownload
        }
        fun setIsOpenBreakpointDownload(isOpenBreakpointDownload:Boolean):DownloadRequestBuilder{
            this.isOpenBreakpointDownload=isOpenBreakpointDownload
            return this
        }
    }

    open inner class BaseRequestBuilder<T>(private val requestNetListener: IRequestNetListener<T>) {
        private var url: String? = null
        private var reqEnv: Int = HttpReqEnv.ASYNCHRONOUS
        fun setUrl(url: String): BaseRequestBuilder<*> {
            this.url = url
            return this
        }

        fun getUrl(): String? {
            return url
        }

        fun getHttpReqEnv(): Int {
            return reqEnv
        }

        fun setReqEnv(@HttpReqEnv reqEnv: Int): BaseRequestBuilder<*> {
            this.reqEnv = reqEnv
            return this
        }

        fun getRequestNetListener(): IRequestNetListener<T> {
            return requestNetListener
        }
    }
}