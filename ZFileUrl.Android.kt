//
//  ZFileUrl.swift
//
//  Created by Tor Langballe on /10/8/2018
//

package com.github.torlangballe.cetrusandroid

import kotlin.math.*
import java.net.URL
//import java.nio.file.Files.isDirectory
import java.io.File
import android.net.Uri
import java.net.URI
import java.net.URISyntaxException


typealias ZOutputStream = String // hack for now

data class ZFileInfo(
        var dataSize:Int = 0,
        var created: ZTime = ZTimeNull,
        var modified: ZTime = ZTimeNull,
        var accessed: ZTime = ZTimeNull,
        var isLocked:Boolean = false,
        var isHidden:Boolean= false,
        var isFolder:Boolean = false,
        var isLink:Boolean = false) {
}

data class ZPathParts (
    var base:String = "", // Base is full path without name
    var fullname:String = "",
    var stub:String = "", // stub is name without extension
    var extension:String = "") {
}

class ZFileUrl: ZUrl {
    companion object {
        //    required init(from decoder: Decoder) throws {
        //        try super.init(from:decoder)
        //    }
        //

        fun GetLegalFilename(filename: String) : String {
            var str = ZStr.UrlEncode(filename)!!
            if (str.length > 200) {
                val a = abs(filename.hashCode())
                str = "$a " + ZStr.Tail(str, chars = 200)
            }
            return str
        }

        fun GetPathParts(path: String) : ZPathParts {
            // place/a.txt = "place" "a.txt" "a" ".txt"
            var url = ZUrl(string = path)
            var parts = ZPathParts()
            if (url.IsEmpty) {
                url = ZUrl(string = "file:///" + path)
            }
            if (!url.IsEmpty) {
                try {
                    val file = File(url.uri!!)
                    parts.fullname = file.name
                    if (parts.fullname != path) {
                        parts.base = file.parentFile.absolutePath
                    }
                    parts.extension= "" + file.extension
                    parts.stub = file.nameWithoutExtension
                }
                catch (e: Exception) {
                    var p = ZStr.TailUntilWithRest(path, "/")
                    parts.fullname = p.first
                    parts.base = p.second
                    p = ZStr.TailUntilWithRest(parts.fullname, ".")
                    parts.stub = p.second
                    parts.extension = p.first
                }
            }
            return parts
        }
    }

    constructor() : super() { }
    constructor(string: String) : super(string) { }
    constructor(nativeUrl:URL) : super(nativeUrl) { }
    constructor(url: ZUrl) : super(url) { }

    constructor(filePath: String, isDir: Boolean = false, dirUnknow: Boolean = false) :
            super(File(filePath).toURI().toURL()) {
    }

    val FilePath: String
        get() {
            if (uri == null) {
                return ""
            }
            val uri = URI(ZStr.UrlDecode(uri!!.path))
            val str = uri.path
            if (IsFolder() && ZStr.Tail(str) != "/") {
                return str + "/"
            }
            return str
        }

    fun OpenOutput(append: Boolean = false) : Pair<ZOutputStream?, ZError?> {
        ZNOTIMPLEMENTED()
        return Pair(null, ZNewError("couldn't make stream"))
    }

    fun IsFolder() : Boolean =
            IsDirectory()

    fun Exists() : Boolean {
        if (uri != null) {
            var file = File(FilePath)
            return file.exists()
        }
        return false
    }

    fun CreateFolder() : Boolean {
        if (uri != null) {
            if (Exists() && IsFolder()) {
                return true
            }
            val dir = File(FilePath)
            if (!dir.mkdirs()) {
                ZDebug.Print("error making folder:", FilePath)
                return false
            }
            return true
        }
        return false
    }

    fun GetDisplayName() : String {
        if (uri != null) {
            return GetName()
        }
        return ""
    }

    fun GetInfo() : Pair<ZFileInfo, ZError?> {
        ZNOTIMPLEMENTED()
        return Pair(ZFileInfo(), null)
    }
    var Modified: ZTime
        get() {
            val (info, err) = GetInfo()
            if (err == null) {
                return info.modified
            }
            return ZTime()
        }
        set(newValue) {
            ZNOTIMPLEMENTED()
        }

    val Created: ZTime
        get() {
            val (info, err) = GetInfo()
            if (err == null) {
                return info.created
            }
            return ZTime()
        }

    val DataSizeInBytes: Int
        get() {
            val (info, err) = GetInfo()
            if (err == null) {
                return info.dataSize
            }
            return -1
        }

    enum class WalkOptions(val rawValue: Int) {
        None(0), SubFolders(1), GetInfo(2), GetInvisible(4);
        companion object : ZEnumCompanion<Int, WalkOptions>(WalkOptions.values().associateBy(WalkOptions::rawValue))
    }

    fun Walk(options: WalkOptions = WalkOptions.None, wildcard: String? = null, foreach: (ZFileUrl, ZFileInfo) -> Boolean) : ZError? {
        // kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/-file-tree-walk/index.html
        ZNOTIMPLEMENTED()
        return null
    }

    fun CopyTo(to: ZFileUrl) : ZError? {
        ZNOTIMPLEMENTED()
        return null
    }

    fun MoveTo(to: ZFileUrl) : ZError? {
        ZNOTIMPLEMENTED()
        return null
    }

    fun LinkTo(to: ZFileUrl) : ZError? {
        // links self to to, i.e self becomes a hard link pointing to to
        ZNOTIMPLEMENTED()
        return null
    }

    fun ResolveSimlinkOrSelf() : ZFileUrl {
        ZNOTIMPLEMENTED()
        return ZFileUrl()
    }

    fun Remove() : ZError? {
        if (uri != null) {
            val f = File(FilePath)
            if (!f.delete()) {
                return ZNewError("Error deleting file: " + FilePath)
            }
        }
        return null
    }

    private fun deleteRecursive(fileOrDirectory: File, top:Boolean) {
        if (fileOrDirectory.isDirectory) {
            for (child in fileOrDirectory.listFiles()!!) {
                deleteRecursive(child, false)
            }
        }
        if (!top) {
            fileOrDirectory.delete()
        }
    }

    fun RemoveContents() : Pair<ZError?, List<String>> {
        val file = File(FilePath)

        deleteRecursive(file, top = true)
        return Pair(null, listOf<String>())
    }

    fun AppendedPath(path: String, isDir: Boolean = false) : ZFileUrl {
        val file = File(FilePath, path)
        return ZFileUrl(filePath = file.absolutePath)
    }
}

//fun |(a: ZFileUrl.WalkOptions, b: ZFileUrl.WalkOptions) : ZFileUrl.WalkOptions =
//        ZFileUrl.WalkOptions(rawValue = a.rawValue | b.rawValue)
//
//fun &(a: ZFileUrl.WalkOptions, b: ZFileUrl.WalkOptions) : Boolean =
//        (a.rawValue & b.rawValue) != 0
