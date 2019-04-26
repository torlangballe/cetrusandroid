//
//  ZFolders.swift
//
//  Created by Tor Langballe on /16/08/18.
//

package com.github.torlangballe.cetrusandroid

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

enum class ZFolderType(val rawValue: Int) {
    preferences(1), caches(4), temporary(8), appSupport(16), temporaryUniqueFolder(256)
}

data class ZFolders (val _dummy: Int = 0) {
    companion object {
        fun GetFileInFolderType(type: ZFolderType, addPath: String = "") : ZFileUrl {
            var furl = ZFileUrl()
            if (type == ZFolderType.preferences) {
                val file = zGetCurrentContext()!!.getApplicationContext().getFilesDir()
                furl = ZFileUrl(filePath = file.path)
            } else if (type == ZFolderType.caches) {
                val tempDir = zGetCurrentContext()!!.externalCacheDir // context being the Activity pointer
                furl = ZFileUrl(filePath = tempDir.path)
            } else if (type == ZFolderType.temporary) {
                val f = GetFileInFolderType(ZFolderType.caches, "temp")
                f.CreateFolder()
                return f.AppendedPath(addPath)
                // File outputFile = File.createTempFile("prefix", "extension", outputDir); useful??
            } else {
                ZNOTIMPLEMENTED()
            }
            return furl.AppendedPath(addPath)
        }
    }
}

private fun copyAsset(name:String, toFile: ZFileUrl) : ZError? {
    val assetManager = zMainActivity!!.getAssets()

    val inStream = assetManager.open(name)
    val fout = File(toFile.FilePath)
    val outStream = FileOutputStream(fout)

    val buffer = ByteArray(1024)
    var read: Int
    while (true) {
        try {
            read = inStream.read(buffer)
            if (read == -1) {
                break
            }
            outStream.write(buffer, 0, read)
        }
        catch (e:IOException) {
            ZDebug.Print("Error copying resource to temp:", name)
            return ZError("Error copying")
        }
    }
    outStream.close()
    val e = toFile.Exists()
    return null
}

fun ZGetResourceFileUrl(subPath:String) : ZFileUrl {
    var furl = ZFolders.GetFileInFolderType(ZFolderType.temporary, "zres")
    val parts = ZStr.Split(subPath, "/")
    parts.forEachIndexed() { i, p ->
        furl = furl.AppendedPath(p)
        if (i != parts.lastIndex && !furl.Exists()) {
            furl.CreateFolder()
        }
    }
    if (!furl.Exists()) {
        copyAsset(subPath, furl)
    }
    return furl
}

