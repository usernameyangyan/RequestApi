package com.request.api.common

import androidx.annotation.IntDef
import com.request.api.common.HttpReqModelType.Companion.DEFAULT_CACHE_LIST
import com.request.api.common.HttpReqModelType.Companion.DEFAULT_CACHE_OBJECT
import com.request.api.common.HttpReqModelType.Companion.DISK_CACHE_LIST_LIMIT_TIME
import com.request.api.common.HttpReqModelType.Companion.DISK_CACHE_OBJECT_LIMIT_TIME
import com.request.api.common.HttpReqModelType.Companion.DISK_CACHE_WITHOUT_NET_LIST
import com.request.api.common.HttpReqModelType.Companion.DISK_CACHE_WITHOUT_NET_OBJECT
import com.request.api.common.HttpReqModelType.Companion.NO_CACHE_LIST
import com.request.api.common.HttpReqModelType.Companion.NO_CACHE_OBJECT

/**
Create by yangyan
Create time:2023/9/5 16:13
Describe:
 */
@Retention(AnnotationRetention.SOURCE)
@IntDef(
    NO_CACHE_OBJECT,
    NO_CACHE_LIST,
    DEFAULT_CACHE_OBJECT,
    DEFAULT_CACHE_LIST,
    DISK_CACHE_LIST_LIMIT_TIME,
    DISK_CACHE_WITHOUT_NET_LIST,
    DISK_CACHE_OBJECT_LIMIT_TIME,
    DISK_CACHE_WITHOUT_NET_OBJECT,
)
annotation class HttpReqModelType {
    companion object {
        //没有缓存
        const val NO_CACHE_OBJECT = 1
        const val NO_CACHE_LIST = 2
        //默认Retrofit缓存
        const val DEFAULT_CACHE_OBJECT = 3
        const val DEFAULT_CACHE_LIST = 4
        //自定义磁盘缓存，返回List
        const val DISK_CACHE_LIST_LIMIT_TIME = 5
        //自定义磁盘缓存，返回Model
        const val DISK_CACHE_OBJECT_LIMIT_TIME = 6
        //自定义磁盘缓存，没有网络返回磁盘缓存，返回List
        const val DISK_CACHE_WITHOUT_NET_LIST = 7
        //自定义磁盘缓存，没有网络返回磁盘缓存，返回Model
        const val DISK_CACHE_WITHOUT_NET_OBJECT = 8
    }
}
