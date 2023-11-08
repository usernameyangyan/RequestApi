package com.request.api.impl

/**
Create by yangyan
Create time:2023/9/5 15:26
Describe:
 */
interface IUploadListener<T>:IRequestNetListener<T>{
    fun onUploadProgress(total: Long,progress: Float)
}