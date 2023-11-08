package com.request.api.convert

import android.text.TextUtils
import com.google.gson.ExclusionStrategy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.request.api.builder.CreateRequestBuilderFactory
import com.request.api.common.ConvertType
import com.request.api.config.ReqConfig
import com.request.api.exception.OtherException
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type

/**
Create by yangyan
Create time:2023/9/6 10:28
Describe:
 */
object RequestGsonConvert {
    /**
     * 转化为object
     *
     * @param reader        json数据
     * @param convertClass 要转化的Class
     * @param <T>
     * @return
    </T> */
    fun <T> fromJsonObject(reader: String, convertClass: Class<*>): T? {
        val type: Type = ParameterizedTypeImpl(
            ReqConfig.instance?.getCommonCovertClass()!!, arrayOf(convertClass)
        )
        val jsonFieldNullHide = ReqConfig.instance?.getJsonFieldNullWillHide()
        return if (!TextUtils.isEmpty(jsonFieldNullHide)) {
            try {
                val jsonObject = JSONObject(reader)
                return if (!jsonObject.has(jsonFieldNullHide) || TextUtils.isEmpty(
                        jsonObject[jsonFieldNullHide!!].toString()
                    )
                ) {
                    getGson().fromJson<T>(reader, type)
                } else {
                    getGsonByExclusionStrategy().fromJson<T>(reader, type)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            null
        } else {
            getGsonByExclusionStrategy().fromJson<T>(reader, type)
        }
    }

    /**
     * 转化为列表
     *
     * @param reader    json数据
     * @param listClass 要转化的Class
     * @param <T>
     * @return
    </T> */
    fun <T> fromJsonArray(reader: String, listClass: Class<*>): T? {
        // 生成List<T> 中的 List<T>
        val listType: Type = ParameterizedTypeImpl(MutableList::class.java, arrayOf(listClass))
        // 根据List<T>生成完整的Result<List<T>>
        val type: Type =
            ParameterizedTypeImpl(ReqConfig.instance?.getCommonCovertClass()!!, arrayOf(listType))
        val jsonFieldNullHide = ReqConfig.instance?.getJsonFieldNullWillHide()
        return if (!TextUtils.isEmpty(jsonFieldNullHide)) {
            try {
                val jsonObject = JSONObject(reader)
                return if (TextUtils.isEmpty(jsonObject[jsonFieldNullHide!!].toString())) {
                    getGson().fromJson<T>(reader, type)
                } else {
                    getGsonByExclusionStrategy().fromJson<T>(reader, type)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            null
        } else {
            getGsonByExclusionStrategy().fromJson<T>(reader, type)
        }
    }


    fun <T> fromJsonNoCommonClass(reader: String?, listClass: Class<*>): T {
        return getGson().fromJson<Any>(reader, listClass) as T
    }

    private var gson: Gson? = null
    private var gsonByExclusionStrategy: Gson? = null
    fun getGson(): Gson {
        if (gson == null) {
            gson = Gson()
        }
        return gson!!
    }

    private fun getGsonByExclusionStrategy(): Gson {
        if (gsonByExclusionStrategy == null) {
            gsonByExclusionStrategy =
                getGsonByExclusionStrategyBuilder(ReqConfig.instance!!.getJsonFieldNullWillHide()!!)
        }
        return gsonByExclusionStrategy!!
    }

    private fun getGsonByExclusionStrategyBuilder(param: String): Gson? {
        val excludeStrategy: ExclusionStrategy = SetterExclusionStrategy(param)
        return GsonBuilder().setExclusionStrategies(excludeStrategy).create()
    }


    /**
     * @param builder
     * @param t
     * @param <T>
     * @return
    </T> */
    fun <T> body2Result(
        builder: CreateRequestBuilderFactory.RequestBuilder<T>,
        t: ResponseBody,
        @ConvertType convertType: Int
    ): T&Any {
        if (builder.getConvertClass() == null) {
            throw OtherException("RequestBuilder convertClass is not init!")
        }
        val a = if (builder.getIsReturnJson()) {
            t.string() as T
        } else if (!builder.isUseCommonConvertClass()) {
            fromJsonNoCommonClass(t.string(), builder.getConvertClass()!!)
        } else if (ReqConfig.instance?.getCommonCovertClass() == null) {
            fromJsonNoCommonClass(t.string(), builder.getConvertClass()!!)
        } else if (ReqConfig.instance?.getCommonCovertClass() != null) {
            when (convertType) {
                ConvertType.LIST -> {
                    fromJsonArray(t.string(), builder.getConvertClass()!!)
                }

                ConvertType.OBJECT -> {
                    fromJsonObject(t.string(), builder.getConvertClass()!!)
                }

                else -> {
                    t.string() as T
                }
            }
        } else {
            t.string() as T
        }
        return a!!
    }

    fun <T> json2Result(builder: CreateRequestBuilderFactory.RequestBuilder<T>, s: String, @ConvertType convertType: Int): T&Any {
        if (builder.getConvertClass() == null) {
            throw OtherException("RequestBuilder convertClass is not init!")
        }
        val a = if (builder.getIsReturnJson()) {
            s as T
        } else if (!builder.isUseCommonConvertClass()) {
            fromJsonNoCommonClass(s, builder.getConvertClass()!!)
        } else if (ReqConfig.instance?.getCommonCovertClass() == null) {
            fromJsonNoCommonClass(s, builder.getConvertClass()!!)
        } else if (ReqConfig.instance?.getCommonCovertClass() != null) {
            when (convertType) {
                ConvertType.LIST -> {
                    fromJsonArray(s, builder.getConvertClass()!!)
                }

                ConvertType.OBJECT -> {
                    fromJsonObject(s, builder.getConvertClass()!!)
                }

                else -> {
                    s as T
                }
            }
        } else {
            s as T
        }
        return a!!
    }

    /**
     * params 转化成Json格式
     * @param builder
     * @param <T>
     * @return
    </T> */
    fun <T> convertParamToJson(builder: CreateRequestBuilderFactory.RequestBuilder<T>): String {
        var json = ""
        if (builder.getReqParamWithoutKey() != null) {
            json = builder.getReqParamWithoutKey().toString()
        } else {
            val stringBuilder = StringBuilder()
            for ((key, value) in builder.getRequestParam()) {
                stringBuilder.append("\"")
                stringBuilder.append(key)
                stringBuilder.append("\"")
                stringBuilder.append(":")
                val jsonStr = value.toString()
                if (jsonStr != "" &&
                    (jsonStr[0] == '[' &&
                            jsonStr[jsonStr.length - 1] == ']' ||
                            jsonStr[0] == '{' &&
                            jsonStr[jsonStr.length - 1] == '}')
                ) {
                    stringBuilder.append(jsonStr)
                } else {
                    when (value) {
                        is Int -> {
                            stringBuilder.append(value)
                        }

                        is Boolean -> {
                            stringBuilder.append(value)
                        }

                        is Long -> {
                            stringBuilder.append(value)
                        }

                        is Float -> {
                            stringBuilder.append(value)
                        }

                        is Double -> {
                            stringBuilder.append(value)
                        }

                        else -> {
                            stringBuilder.append("\"")
                            stringBuilder.append(value)
                            stringBuilder.append("\"")
                        }
                    }
                }
                stringBuilder.append(",")
            }
            val str = stringBuilder.toString()
            json = str.substring(0, str.length - 1)
            json = "$json}"
            if (json == "}") {
                json = "{$json"
            }
        }
        return json
    }
}