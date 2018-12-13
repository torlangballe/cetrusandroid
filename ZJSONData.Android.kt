//
//  ZJSONData.Android.kt
//
//  Created by Tor Langballe on /11/10/18.
//

package com.github.torlangballe.cetrusandroid

import kotlinx.serialization.*
import kotlinx.serialization.json.JSON
import java.lang.Exception

typealias ZJSON = JSON

inline fun <reified T, reified U> ZData.Decode(serializer: KSerializer<T>, t:U): Pair<U?, ZError?> {
    try {
//        val d = JSON.parse(T::class.serializer(), GetString())
        val d = JSON.parse(serializer, GetString())
        return Pair(d as U, null)
    } catch (e:Exception) {
        ZDebug.Print("ZData.Decode err:", e.localizedMessage)
        return Pair(null, ZNewError(e.localizedMessage))
    }
}

inline fun <reified T> ZData.Companion.EncodeJson(serializer: KSerializer<T>, item:T) : Pair<ZData?, ZError?> {
    try {
        var s = JSON.stringify(serializer, item)
        return Pair(ZData(utfString = s), null)
    } catch (e:Exception) {
        ZDebug.Print("ZData.EncodeJson err:", e.localizedMessage)
        return Pair(null, ZNewError(e.localizedMessage))
    }
}

