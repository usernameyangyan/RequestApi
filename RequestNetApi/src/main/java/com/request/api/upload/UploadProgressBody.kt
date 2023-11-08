package com.request.api.upload

import android.os.Handler
import android.os.Looper
import com.request.api.builder.CreateRequestBuilderFactory
import com.request.api.impl.IUploadListener
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Sink
import okio.buffer
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
/**
Create by yangyan
Create time:2023/9/6 09:29
Describe:
 */
class UploadProgressBody(
    private val requestBody: RequestBody,
    private val requestBuilder: CreateRequestBuilderFactory.RequestBuilder<*>
) : RequestBody() {
    //包装完成的BufferedSink
    private var bufferedSink: BufferedSink? = null
    private val mainHandler: Handler = Handler(Looper.getMainLooper())
    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    override fun contentLength(): Long {
        return requestBody.contentLength()
    }

    override fun writeTo(sink: BufferedSink) {
        if (bufferedSink == null) {
            //包装
            bufferedSink = sink(sink).buffer()
        }
        //写入
        requestBody.writeTo(bufferedSink!!)
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink!!.flush()

    }

    /**
     * 写入，回调进度接口
     * @param sink Sink
     * @return Sink
     */
    private fun sink(sink: Sink): Sink {
        return object : ForwardingSink(sink) {
            //当前写入字节数
            var bytesWritten = 0L

            //总字节长度，避免多次调用contentLength()方法
            var contentLength = 0L
            var preValue = -1f

            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (contentLength == 0L) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength()
                }
                //增加当前写入的字节数
                bytesWritten += byteCount
                //回调
                val fmt = DecimalFormat("#0.00")
                fmt.roundingMode = RoundingMode.DOWN
                val s = fmt.format((bytesWritten.toFloat() / contentLength).toDouble())
                val progress = s.toFloat()
                if (preValue != progress) {
                    mainHandler.post {
                        (requestBuilder.getRequestNetListener() as IUploadListener).onUploadProgress(
                            contentLength,
                            progress
                        )
                    }
                    preValue = progress
                }

            }
        }
    }
}