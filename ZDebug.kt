
//
//  ZDebug.swift
//  Zed
//
//  Created by Tor Langballe on /26/11/15.
//  Copyright © 2015 Capsule.fm. All rights reserved.
//
package com.github.torlangballe.cetrusandroid

fun ZDebug.Companion.Print(vararg items: Any?, separator: String = " ", terminator: String = "\n") {
    var str = ""
    if (lastStampTime.Since() > 3.0) {
        lastStampTime = ZTime.Now()
        str = lastStampTime.GetString(format = "============= yy-MM-dd' 'HH:mm:ss =============\n")
    }
    for ((i, item) in items.withIndex()) {
        if (i != 0) {
            str += separator
        }
        str += "${item ?: "<nil>"}"
    }
    mutex.Lock()
    if (storePrintLines != 0) {
        if (storedLines.size > storePrintLines) {
            storedLines.removeFirst()
        }
        storedLines.append(str)
    }
    for (h in printHooks) {
        h(str)
    }
    mutex.Unlock()
    basePrint(str, terminator = terminator)
}

fun ZDebug.Companion.ErrorOnRelease() {
    if (IsRelease()) {
        var n = 100
        while (n > 0) {
            ZDebug.Print("Should not run on ")
            n -= 1
        }
    }
}

fun ZDebug.Companion.LoadSavedLog(prefix: String) {
    val file = ZFolders.GetFileInFolderType(ZFolderType.temporary, addPath = prefix + "/zdebuglog.txt")
    val (str, _) = ZStr.LoadFromFile(file)
    storedLines = ZStr.Split(str, sep = "\n").writable()
}

fun ZDebug.Companion.AppendToFileAndClearLog(prefix: String) {
    val file = ZFolders.GetFileInFolderType(ZFolderType.temporary, addPath = prefix + "/zdebuglog.txt")
    if (file.DataSizeInBytes > 5 * 1024 * 1024) {
        file.Remove()
        storedLines.insert("--- ZDebug.Cleared early part of large stored log.", at = 0)
    }
    storedLines.removeAll()
}
