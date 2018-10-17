package com.github.torlangballe.cetrusandroid

//
//  ZData.Android.kt
//
//  Created by Tor Langballe on /15/08/18.
//

class ZData(val data:ByteArray = ByteArray(0)) {
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
        return data.toString()
    }

    fun GetHexString(): String {
        var hex = ""
        data.forEach {
            hex += ZStr.Format("%02x", it)
        }
        return hex
    }

    fun SaveToFile(file: ZFileUrl): ZError? {
        ZNOTIMPLEMENTED()
        return null
    }

    fun LoadFromFile(file: ZFileUrl): ZError? {
        ZNOTIMPLEMENTED()
        return null
    }

    constructor(utfString: String) : this(utfString.toByteArray()) {
    }
}
