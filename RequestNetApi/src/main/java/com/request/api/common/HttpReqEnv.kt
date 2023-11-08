package com.request.api.common

import androidx.annotation.IntDef
import com.request.api.common.HttpReqEnv.Companion.ASYNCHRONOUS
import com.request.api.common.HttpReqEnv.Companion.SYNCHRONIZATION

/**
Create by yangyan
Create time:2023/9/5 16:13
Describe:
 */
@Retention(AnnotationRetention.SOURCE)
@IntDef(
    ASYNCHRONOUS,
    SYNCHRONIZATION,
)
annotation class HttpReqEnv {
    companion object {
        const val ASYNCHRONOUS = 0
        const val SYNCHRONIZATION = 1
    }
}
