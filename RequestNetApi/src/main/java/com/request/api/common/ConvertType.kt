package com.request.api.common

import androidx.annotation.IntDef
import com.request.api.common.ConvertType.Companion.LIST
import com.request.api.common.ConvertType.Companion.OBJECT

/**
Create by yangyan
Create time:2023/9/5 16:13
Describe:
 */
@Retention(AnnotationRetention.SOURCE)
@IntDef(
    OBJECT,
    LIST,
)
annotation class ConvertType {
    companion object {
        const val OBJECT = 0
        const val LIST = 1
    }
}
