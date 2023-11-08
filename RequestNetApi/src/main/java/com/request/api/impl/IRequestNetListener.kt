package com.request.api.impl
import com.request.api.exception.BaseException
/**
Create by yangyan
Create time:2023/9/5 15:15
Describe:
 */
interface IRequestNetListener<T> {
    fun onNext(result: T?)
    fun onError(e: BaseException)
}