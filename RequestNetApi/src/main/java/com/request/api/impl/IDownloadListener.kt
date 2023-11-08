package com.request.api.impl

/**
Create by yangyan
Create time:2023/9/5 15:25
Describe:
 */
interface IDownloadListener<T>:IRequestNetListener<T> {
    fun onDownloadProgress(total:Long,currentLength:Long,progress:Float)
}