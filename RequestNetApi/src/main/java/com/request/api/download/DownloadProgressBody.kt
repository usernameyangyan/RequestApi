package com.request.api.download

import android.os.Handler
import android.os.Looper
import com.request.api.builder.CreateRequestBuilderFactory
import com.request.api.impl.IDownloadListener
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.IOException
import okio.Source
import okio.buffer
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat


/**
Create by yangyan
Create time:2023/9/6 08:46
Describe:
 */
class DownloadProgressBody(
    private val responseBody: ResponseBody,
    private val requestBuilder: CreateRequestBuilderFactory.DownloadRequestBuilder,
) : ResponseBody() {
    private val mainHandler: Handler = Handler(Looper.getMainLooper())
    private val downUrl =
        requestBuilder.getDownloadFilePath() + File.separator + requestBuilder.getDownloadFileName()
    private var bufferedSource: BufferedSource? = null
    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            //包装
            bufferedSource = source(responseBody.source()).buffer()
        }
        return bufferedSource!!
    }

    /**
     * 读取，回调进度接口
     *
     * @param source Source
     * @return Source
     */
    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            //当前读取字节数
            var totalBytesRead = 0L
            var preValue = -1f
            var file = File(downUrl)

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                //回调，如果contentLength()不知道长度，会返回-1
                if (bytesRead != -1L) {
                    val localSize = file.length() //本地已下载的长度
                    val trueTotal =
                        localSize + responseBody.contentLength() - totalBytesRead //文件真实长度
                    var progress = localSize.toFloat() / trueTotal
                    if (progress > 0) {
                        val fmt = DecimalFormat("#0.00")
                        fmt.roundingMode = RoundingMode.DOWN
                        val s = fmt.format(progress.toDouble())
                        progress = s.toFloat()
                    }
                    if (preValue != progress) {
                        mainHandler.post {
                            (requestBuilder.getRequestNetListener() as IDownloadListener).onDownloadProgress(
                                trueTotal,
                                localSize,
                                progress
                            )
                        }
                        preValue = progress
                    }
                }
                return bytesRead
            }
        }
    }
}