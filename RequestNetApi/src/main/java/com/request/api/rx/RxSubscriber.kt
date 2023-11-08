package com.request.api.rx

import com.request.api.exception.BaseException
import com.request.api.exception.Description
import com.request.api.exception.RequestExceptionCode
import io.reactivex.observers.DisposableObserver

/**
Create by yangyan
Create time:2023/9/6 16:07
Describe:
 */
abstract class RxSubscriber<T>: DisposableObserver<T>() {
    override fun onComplete() {
        if (isDisposed) {
            return
        } else {
            dispose()
        }
    }

    override fun onError(e: Throwable) {
        if (isDisposed) {
            return
        } else {
            dispose()
        }
        _onError(BaseException(RequestExceptionCode.RESPONSE_ERROR, Description(-1,e.message)))
    }

    override fun onNext(t: T & Any) {
        if (isDisposed) {
            return
        } else {
            dispose()
        }
        _onNext(t)
    }

    /**
     * 定义处理事件
     */
    abstract fun _onNext(t: T)
    abstract fun _onError(e: BaseException)
}