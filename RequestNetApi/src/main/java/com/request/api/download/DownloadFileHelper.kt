package com.request.api.download

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile

/**
Create by yangyan
Create time:2023/9/6 14:15
Describe:
 */
object DownloadFileHelper {
    fun createDownInfo(url: String): DownloadInfo {
        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .build()
        val contentLength: Long = getContentLength(okHttpClient, url) //获得文件大小
        val name = url.substring(url.lastIndexOf("/"))
        return DownloadInfo(url, contentLength, 0, name, "", false)
    }

    /**
     * 获取下载长度
     *
     * @param downloadUrl
     * @param mClient
     * @return
     */
    private fun getContentLength(mClient: OkHttpClient, downloadUrl: String): Long {
        val request: Request = Request.Builder()
            .url(downloadUrl)
            .build()
        try {
            val response = mClient.newCall(request).execute()
            if (response.isSuccessful) {
                val contentLength = response.body!!.contentLength()
                response.close()
                return if (contentLength == 0L) -1 else contentLength
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return -1
    }

    fun getRealFileName(
        downloadInfo: DownloadInfo,
        filePath: String,
        fileName: String,
        isBreakPoint: Boolean
    ): DownloadInfo {
        var downloadLength: Long = 0
        val contentLength: Long = downloadInfo.total
        var file = File(filePath + File.separator + fileName)
        if (file.exists()) {
            //找到了文件,代表已经下载过,则获取其长度
            downloadLength = file.length()
        }
        //之前下载过,需要重新来一个文件
        if (downloadLength >= contentLength || !isBreakPoint) {
            file.delete()
        }
        file = File(filePath + File.separator + fileName)
        //设置改变过的文件名/大小
        downloadInfo.progress=file.length()
        downloadInfo.fileName=file.name
        return downloadInfo
    }


    /**
     * 写入文件
     *
     * @throws IOException
     */
    fun writeCache(
        responseBody: ResponseBody,
        url: String,
        filePath: String,
        fileName: String
    ): DownloadInfo {
        var inputStream: InputStream? = null
        var raf: RandomAccessFile? = null
        val path = filePath + File.separator + fileName
        val file = File(path)
        var isFinish = false
        val fileDir = File(filePath)
        if (!fileDir.exists()) {
            // 创建文件夹
            fileDir.mkdirs()
        }
        try {
            raf = RandomAccessFile(path, "rw")
            inputStream = responseBody.byteStream()
            val fileReader = ByteArray(4096)
            raf.seek(file.length())
            while (true) {
                val read = inputStream.read(fileReader)
                if (read == -1) {
                    isFinish = true
                    break
                }
                raf.write(fileReader, 0, read)
            }
        } catch (_: FileNotFoundException) {
        } catch (_: IOException) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (raf != null) {
                try {
                    raf.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return DownloadInfo(url, file.length(), file.length(), fileName, filePath, isFinish)
    }
}