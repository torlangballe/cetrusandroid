package com.github.torlangballe.cetrusandroid

//
//  ZJSONData.Android.kt
//
//  Created by Tor Langballe on /11/10/18.
//

import kotlinx.serialization.*
import kotlinx.serialization.json.JSON
import java.lang.Exception

typealias ZJSON = JSON

fun <T> ZData.Decode(serializer: KSerializer<T>): Pair<T, ZError?> {
    val d = JSON.parse(serializer, GetString())
    return Pair(d as T, null)
}

fun <T: Serializable> ZData.Companion.EncodeJson(serializer: KSerializer<T>, item:T) : ZData? {
    try {
//        var s = JSON.stringify(item.annotationClass.serializer())
        var s = JSON.stringify(serializer, item)
        return ZData(utfString = s)
    } catch (e:Exception) {
        ZDebug.Print("ZData.EncodeJson err:", e.localizedMessage)
        return null
    }
}

