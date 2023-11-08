package com.request.api.utils

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
Create by yangyan
Create time:2023/9/6 16:44
Describe:
 */
object RequestFileUtils {
    fun isCacheDataFailure(filePath: String?, hours: Int): Boolean {
        val cache_time_millis = hours * 60 * 60000 // 把小时转换为毫秒
        var failure = false
        val data = File(filePath)
        if (data.exists()
            && System.currentTimeMillis() - data.lastModified() > cache_time_millis
        ) {
            failure = true
        } else if (!data.exists()) {
            failure = true
        }
        return failure
    }

    /**
     * 读取文本文件中的内容
     *
     * @param strFilePath 文件详细路径
     * @return
     */
    fun readTxtFile(strFilePath: String): String {
        var content = "" // 文件内容字符串
        // 打开文件
        val file = File(strFilePath)
        // 如果path是传递过来的参数，可以做一个非目录的判断
        if (!file.isDirectory && file.exists()) {
            try {
                val inStream: InputStream = FileInputStream(file)
                val inputReader = InputStreamReader(inStream)
                val buffReader = BufferedReader(inputReader)
                var line: String
                // 分行读取
                while (buffReader.readLine().also { line = it } != null) {
                    content += if (strFilePath.contains("ggid")) {
                        line
                    } else {
                        line.trimIndent()
                    }
                }
                inStream.close()
            } catch (_: FileNotFoundException) {

            } catch (_: IOException) {
            }
        }
        return content
    }

    /**
     * 写文件
     *
     * @param filePath
     * @param fileName
     * @param content
     * @param append   是否添加在原内容的后边
     * @return
     */
    fun writerTxtFile(
        filePath: String,
        fileName: String,
        content: String,
        append: Boolean=false,
        fileBack: FileBack?=null
    ): Boolean {
        val strFile = "$filePath/$fileName"

        // 判断目录是否存在。如不存在则创建一个目录
        var file = File(filePath)
        if (!file.exists()) {
            file.mkdirs()
        }
        file = File(strFile)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val out: FileOutputStream
        try {
            out = FileOutputStream(strFile, append)
            out.write(content.toByteArray(charset("UTF-8")))
            out.close()
            fileBack?.success(content)
        } // true表示在文件末尾添加
        catch (e: Exception) {
            fileBack?.error(e)
        }
        return true
    }

    interface FileBack {
        fun success(text: String?)
        fun error(e: java.lang.Exception?)
    }

}