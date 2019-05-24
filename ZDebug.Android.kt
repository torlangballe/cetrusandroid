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
import kotlinx.io.IOException
import java.io.BufferedReader
import java.io.InputStreamReader

open class ZDebug(var dummy:Int = 0) {
    companion object {
        val mutex = ZMutex()
        var storePrintLines = 0
        var storedLines = mutableListOf<String>()
        var lastStampTime = ZTimeNull
        var printHooks = mutableListOf<(String) -> Unit>()
        var logAllOutput = false

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
            return (zGetCurrentContext()!!.getApplicationInfo().flags and ApplicationInfo.FLAG_DEBUGGABLE) === 0
        }

        fun IsMinIOS11(): Boolean = false

        fun HasPermission(permission: String, request:Boolean = true): Boolean {
            val perm = ContextCompat.checkSelfPermission(zGetCurrentContext()!!, permission)
            if (perm === PackageManager.PERMISSION_GRANTED) {
                return true
            }
            if (request && perm == PackageManager.PERMISSION_DENIED) {
                zMainActivity!!.requestPermissions(arrayOf(permission), 1)
//                HasPermission(permission, request = false)
            }
            return false
        }

        fun Init(logAll: Boolean = false) {
            if (logAll) {
                ZGetBackgroundQue().async {
                    startlLogAllOutput()
                }
            }
        }
    }
}

fun ZNOTIMPLEMENTED() {
    throw Error("Not implemented yet.")
}

fun startlLogAllOutput() {
    try {
        val process = Runtime.getRuntime().exec("logcat")
        val bufferedReader = BufferedReader(
            InputStreamReader(process.inputStream)
        )
        ZDebug.logAllOutput = true
        val log = StringBuilder()
        while (true) {
            val line = bufferedReader.readLine()
            if (line == null) {
                break
            }
            ZDebug.mutex.Lock()
            ZDebug.storedLines.append(line)
            ZDebug.mutex.Unlock()
            for (h in ZDebug.printHooks) {
                h(line)
            }
        }
    } catch (e: IOException) {
        ZDebug.Print("getOutput err:", e)
    }
}