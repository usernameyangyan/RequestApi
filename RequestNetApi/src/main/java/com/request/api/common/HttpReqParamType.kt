package com.request.api.common

import androidx.annotation.IntDef
import com.request.api.common.HttpReqParamType.Companion.BODY_DELETE
import com.request.api.common.HttpReqParamType.Companion.BODY_PATCH
import com.request.api.common.HttpReqParamType.Companion.BODY_POST
import com.request.api.common.HttpReqParamType.Companion.BODY_PUT
import com.request.api.common.HttpReqParamType.Companion.DEFAULT_DELETE
import com.request.api.common.HttpReqParamType.Companion.DEFAULT_GET
import com.request.api.common.HttpReqParamType.Companion.DEFAULT_POST
import com.request.api.common.HttpReqParamType.Companion.JSON_DELETE
import com.request.api.common.HttpReqParamType.Companion.JSON_PATCH
import com.request.api.common.HttpReqParamType.Companion.JSON_POST
import com.request.api.common.HttpReqParamType.Companion.JSON_PUT
import com.request.api.common.HttpReqParamType.Companion.MULTI_PART_POST
import com.request.api.common.HttpReqParamType.Companion.MULTI_PART_PUT

/**
Create by yangyan
Create time:2023/9/5 16:13
Describe:
 */
@Retention(AnnotationRetention.SOURCE)
@IntDef(
    DEFAULT_GET,
    DEFAULT_POST,
    MULTI_PART_POST,
    DEFAULT_DELETE,
    MULTI_PART_PUT,
    BODY_PUT,
    BODY_POST,
    BODY_PATCH,
    BODY_DELETE,
    JSON_DELETE,
    JSON_POST,
    JSON_PUT,
    JSON_PATCH
)
annotation class HttpReqParamType {
    companion object {
        const val DEFAULT_GET = 0
        const val DEFAULT_POST = 1
        const val MULTI_PART_POST = 2
        const val DEFAULT_DELETE = 4
        const val MULTI_PART_PUT = 5
        const val BODY_PUT = 6
        const val BODY_POST = 7
        const val BODY_PATCH = 8
        const val BODY_DELETE = 9
        const val JSON_DELETE = 10
        const val JSON_POST = 11
        const val JSON_PUT = 12
        const val JSON_PATCH = 13
    }
}
