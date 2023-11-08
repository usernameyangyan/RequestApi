package com.request.api.convert
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
Create by yangyan
Create time:2023/9/6 10:29
Describe:
 */
class ParameterizedTypeImpl(private val raw: Class<*>,private val args:Array<Type>): ParameterizedType {
    override fun getActualTypeArguments(): Array<Type> {
        return args
    }

    override fun getRawType(): Type {
        return raw
    }

    override fun getOwnerType(): Type? {
        return null
    }
}