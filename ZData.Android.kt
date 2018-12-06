package com.github.torlangballe.cetrusandroid

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception

//
//  ZData.Android.kt
//
//  Created by Tor Langballe on /15/08/18.
//

open class ZData(var data:ByteArray = ByteArray(0)) {
    constructor(fileUrl: ZFileUrl) : this(ByteArray(0)) {
        ZNOTIMPLEMENTED()
    }

    val length: Int
        get() {
            return data.size
        }

    companion object {
        fun FromUrl(url: ZUrl): Pair<ZData?, ZError?> {
            ZNOTIMPLEMENTED()
            return Pair(null, null)
        }

        fun FromHex(hex:String) : ZData {
            ZNOTIMPLEMENTED()
            return ZData(ByteArray(0))
        }
    }

    fun GetString(): String {
        return String(bytes = data)
    }

    fun GetHexString(): String {
        var hex = ""
        data.forEach {
            hex += ZStr.Format("%02x", it)
        }
        return hex
    }

    fun SaveToFile(file: ZFileUrl): ZError? {
        val f = File(file.FilePath)
        val bin = FileOutputStream(f)
        try {
            bin.write(data)
        } catch(e:Exception) {
            return ZNewError("ZData.SaveToFile: " + e.toString())
        } finally {
            bin.close()
        }
        return null
    }

    fun LoadFromFile(file: ZFileUrl): ZError? {
        if (!file.Exists()) {
            return ZNewError("File doesn't exist")
        }
        val f = File(file.FilePath)
        val length = f.length().toInt()
        var contents = ""
        if (length > 0) {
            val bytes = ByteArray(length)
            val bin = FileInputStream(f)
            try {
                bin.read(bytes)
            } finally {
                bin.close()
            }
            data = bytes
        }
        return null
    }

    constructor(utfString: String) : this(utfString.toByteArray()) {
    }
}
