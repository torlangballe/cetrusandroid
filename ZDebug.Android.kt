//
//  ZDebugAndroid.swift
//
//  Created by Tor Langballe on /26/11/15.
//  Copyright © 2018 Capsule.fm. All rights reserved.
//

package com.github.torlangballe.cetrusandroid

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

        fun IsRelease(): Boolean =
        // not implemented yet!
                false

        fun IsMinIOS11(): Boolean = false
    }
}

fun ZNOTIMPLEMENTED() {
    throw Error("Not implemented yet.")
}