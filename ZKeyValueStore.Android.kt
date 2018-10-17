//
//  ZKeyValueStore.Androi.kt
//
//  Created by Tor Langballe on /15/08/18.
//

package com.github.torlangballe.cetrusandroid

import android.content.Context

// https://developer.android.com/training/data-storage/shared-preferences

object ZKeyValueStore {
    val saver = zMainActivity!!.getPreferences(Context.MODE_PRIVATE).edit()
    val store = zMainActivity!!.getPreferences(Context.MODE_PRIVATE)

    var keyPrefix = ""

    fun ObjectForKey(key: String) : Any? {
        ZNOTIMPLEMENTED()
        return null
    }

    fun SetObject(anObject: Any?, key: String) {
        ZNOTIMPLEMENTED()
    }

    fun StringForKey(key: String) : String? {
        return store.getString(key, null)
    }

    fun ArrayForKey(key: String) : List<Any>? {
        ZNOTIMPLEMENTED()
        return listOf<Any>()
    }

    fun DictionaryForKey(key: String) : Map<String, Any>? {
        ZNOTIMPLEMENTED()
        return mapOf<String, Any>()
    }

    fun DataForKey(key: String) : ZData? {
        ZNOTIMPLEMENTED()
        return null
    }

    fun IntForKey(key: String) : Int {
        return store.getInt(key, 0)
    }

    fun DoubleForKey(key: String) : Double {
        return store.getFloat(key, 0f).toDouble()
    }

    fun TimeForKey(key: String) : ZTime? {
        ZNOTIMPLEMENTED()
        return null
    }

    fun BoolForKey(key: String, def: Boolean? = null) : Boolean {
        return store.getBoolean(key, false)
    }

    fun IncrementInt(key: String, sync: Boolean = true, inc: Int = 1) : Int {
        ZNOTIMPLEMENTED()
        return 0
    }

    fun RemoveForKey(key: String, sync: Boolean = true) {
        ZNOTIMPLEMENTED()
    }

    fun SetString(string: String?, key: String, sync: Boolean = true) {
        ZNOTIMPLEMENTED()
        if (sync) {
            Synch()
        }
    }

    fun SetData(data: ZData?, key: String, sync: Boolean = true) {
        ZNOTIMPLEMENTED()
        if (sync) {
            Synch()
        }
    }

    fun SetArray(anArray: List<Any>?, key: String, sync: Boolean = true) {
        ZNOTIMPLEMENTED()
        if (sync) {
            Synch()
        }
    }

    fun SetDictionary(aDictionary: Map<String, Any>?, key: String, sync: Boolean = true) {
        ZNOTIMPLEMENTED()
        if (sync) {
            Synch()
        }
    }

    fun SetInt(value: Int, key: String, sync: Boolean = true) {
        saver.putInt(key, value)
        if (sync) {
            Synch()
        }
    }

    fun SetDouble(value: Double, key: String, sync: Boolean = true) {
        saver.putFloat(key, value.toFloat())
        if (sync) {
            Synch()
        }
    }

    fun SetBool(value: Boolean, key: String, sync: Boolean = true) {
        saver.putBoolean(key, value)
        if (sync) {
            Synch()
        }
    }

    fun SetTime(value: ZTime, key: String, sync: Boolean = true) {
        ZNOTIMPLEMENTED()
    }

    fun Synch() : Boolean {
        saver.apply()
        return true
    }

    fun ForAllKeys(got: (key: String) -> Unit) {
        ZNOTIMPLEMENTED()
    }
}

private fun makeKey(key: String) : String {
    if (key.startsWith("/")) {
        return key
    }
    return ZKeyValueStore.keyPrefix + key
}