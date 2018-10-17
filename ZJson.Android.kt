//
//  XJson.swift
//
//  Created by Tor Langballe on 13.09.2018
//

package com.github.torlangballe.cetrusandroid

import org.json.JSONArray
import org.json.JSONObject

class XJSON {
    var jarray: JSONArray? = null
    var jobject: JSONObject? = null
    var error: ZError? = null

    companion object {
        fun FromString(rawUtf8String: String) : XJSON? {
            val data = ZData(utfString = rawUtf8String)
            if (data != null) {
                return XJSON(zdata = data)
            }
            return null
        }

        fun JDict() : XJSON {
            val j = XJSON()
            j.jobject = JSONObject()
            return j
        }
    }

    constructor() {
    }

    constructor(zdata: ZData) {
        jarray = JSONArray(zdata.data)
    }

    fun AddKeyValue(key:String, value:Any) {
        if (jobject != null) {
            jobject!!.put(key, value)
        }
    }

    fun AddValue(value:Any) {
        if (jarray != null) {
            jarray!!.put(value)
        }
    }

    fun GetKeyValue(key:String) : Any? {
        if (jobject != null) {
            return jobject!!.get(key)
        }
        return null
    }

    fun GetString(key:String) : String {
        val v = GetKeyValue(key)
        if (v != null) {
            return v.toString()
        }
        return ""
    }

    fun GetTime(key:String) : ZTime? {
        val v = GetKeyValue(key)
        if (v != null) {
            val str = "$v"
            var t = ZTime(iso8601Z = str)
            if (t == null) {
                t = ZTime(format = ZTimeIsoFormatWithZone, dateString = str)
            }
            return t
        }
        return null
    }

    val data: ZData?
        get() {
            var str = ""
            if (jarray != null) {
                str = jarray!!.toString(2)
            } else if (jobject != null) {
                str = jobject!!.toString(2)
            }
            return ZData(utfString = str)
        }

    val stringStringDictionaryValue: Map<String, String>
        get() {
            var dict = mutableMapOf<String, String>()
            if (jobject != null) {
                for (k in jobject!!.keys()) {
                    val v = jobject!![k]
                    dict.put(k, "$v")
                }
            }
            return dict
        }

    val dictionaryObjectValue: Map<String, Any>
        get() {
            var dict = mutableMapOf<String, Any>()
            if (jobject != null) {
                for (k in jobject!!.keys()) {
                    val v = jobject!![k]
                    dict.put(k, v)
                }
            }
            return dict
        }

    val stringArrayValue: List<String>
        get() {
            var strings = mutableListOf<String>()
            if (jarray != null) {
                for (i in 0 .. jarray!!.length()) {
                    val j = jarray!!.get(i)
                    strings.append(j.toString())
                }
            }
            return strings
        }

    val intArrayValue: List<Int>
        get() {
            var strings = mutableListOf<Int>()
            if (jarray != null) {
                for (i in 0 .. jarray!!.length()) {
                    val j = jarray!!.get(i)
                    print(j)
                }
            }
            return strings
        }
}
