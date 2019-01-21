//
//  ZDebugAndroid.swift
//
//  Created by Tor Langballe on /26/11/15.
//  Copyright © 2018 Capsule.fm. All rights reserved.
//

package com.github.torlangballe.cetrusandroid

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

open class ZDebug(var dummy:Int = 0) {
    companion object {
        val mutex = ZMutex()
        var storePrintLines = 0
        var storedLines = mutableListOf<String>()
        var lastStampTime = ZTimeNull
        var printHooks = mutableListOf<(String) -> Unit>()

        fun basePrint(vararg items: Any?, separator: String = " ", terminator: String = "\n") {
            var str = ""
            for (i in items) {
                if (!str.isEmpty()) {
                    str += separator
                }
                str += "$i"
            }
            str += terminator
            print(str)
        }

        fun IsRelease(): Boolean {
            return (zMainActivityContext!!.getApplicationInfo().flags and ApplicationInfo.FLAG_DEBUGGABLE) === 0
        }

        fun IsMinIOS11(): Boolean = false

        fun HasPermission(permission: String, request:Boolean = true): Boolean {
            val perm = ContextCompat.checkSelfPermission(zMainActivityContext!!, permission)
            if (perm === PackageManager.PERMISSION_GRANTED) {
                return true
            }
            if (request && perm == PackageManager.PERMISSION_DENIED) {
                zMainActivity!!.requestPermissions(arrayOf(permission), 1)
//                HasPermission(permission, request = false)
            }
            return false
        }
    }
}

fun ZNOTIMPLEMENTED() {
    throw Error("Not implemented yet.")
}