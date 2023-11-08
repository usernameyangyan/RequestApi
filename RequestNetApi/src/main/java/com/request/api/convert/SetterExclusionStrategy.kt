package com.request.api.convert

import android.text.TextUtils
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes

/**
Create by yangyan
Create time:2023/9/6 10:42
Describe:
 */
class SetterExclusionStrategy(private val field:String): ExclusionStrategy {
    override fun shouldSkipField(f: FieldAttributes): Boolean {
        if (!TextUtils.isEmpty(field)) {
            if (f.name == field) {
                /** true 代表此字段要过滤  */
                return true
            }
        }
        return false
    }

    override fun shouldSkipClass(clazz: Class<*>?): Boolean {
        return false
    }
}