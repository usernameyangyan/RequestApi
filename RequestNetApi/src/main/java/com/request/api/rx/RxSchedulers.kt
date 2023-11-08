package com.request.api.rx

import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
Create by yangyan
Create time:2023/9/6 16:00
Describe:
 */
object RxSchedulers {
    fun <T> threadToMain(): ObservableTransformer<T, T> {
        return ObservableTransformer { observable ->
            observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }
}