//
//  ZUrlAndroid.swift
//
//  Created by Tor Langballe on /30/10/15.
//

package com.github.torlangballe.cetrusandroid

import java.net.URL
import java.net.URI
//import java.nio.file.Files.isDirectory
import java.io.File
import android.content.Intent
import android.net.Uri
import java.lang.Exception
import java.net.MalformedURLException
import java.nio.file.Paths

open class ZUrl {
    var uri: URI? = null

    constructor() {
        uri = null
    }

    constructor(string: String) {
        try {
            uri = URI(string)
        } catch (e: MalformedURLException) {
            uri = null
        }
    }

    constructor(nativeUrl:URL) {
        uri = nativeUrl.toURI()
    }

    constructor(nativeUri:URI) {
        uri = nativeUri
    }

    constructor(url: ZUrl) {
        uri = url.uri
    }

    val IsEmpty: Boolean
        get() = (uri == null)

    fun IsDirectory() : Boolean {
        val protocol = uri?.scheme ?: false
        if (protocol == "file") {
            return File(toUrl()?.getFile()).isDirectory()
        }
        // TODO: Can be online web dir too?
        return false
    }

    private fun toUrl() : URL? {
        if (uri == null) {
            return null
        }
        try {
            val u = uri!!.toURL()
            return u
        } catch (e: Exception) {
            ZDebug.Print("ZUrl.toUrl error:", e.localizedMessage)
            return null
        }
    }

    fun OpenInBrowser(inApp: Boolean, notInAppForSocial: Boolean = true) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
        zMainActivityContext!!.startActivity(browserIntent)
    }

    fun GetName() : String {
        if (uri != null) {
            return Paths.get(uri!!.getPath()).getFileName().toString()
        }
        return ""
    }
    val Scheme: String
        get()  {
            return uri?.scheme ?: ""
        }

    val Host: String
        get() {
            return uri?.host ?: ""
        }

    val Port: Int
        get() {
            return uri?.port ?: 0
        }

    val AbsString: String
        get() {
            return uri?.toString() ?: ""
        }

    val ResourcePath: String
        get() {
            return uri?.path ?: ""
        }

    var Extension: String
        get() {
            // TODO: Fix quick and dirty
            val h = ZStr.HeadUntil(AbsString, "?")
            return ZStr.TailUntil(h, ".")
        }
        set(newValue) {
            ZNOTIMPLEMENTED()
        }
    val Anchor: String
            get() {
                return toUrl()?.ref ?: ""
            }

    val Parameters: Map<String, String>
        get() {
            val q = uri?.query
            if (q != null) {
                return ParametersFromString(q)
            }
            val tail = ZStr.TailUntil((uri?.toString()) ?: "", sep = "?")
            if (tail != "") {
                return ParametersFromString(tail)
            }
            return mapOf<String, String>()
        }

    companion object {
        // handle error

        // can have completion handler too
        // if lastPathComponent is nil, ?? returns ""
        // called fragment really
        fun ParametersFromString(parameters: String) : Map<String, String> {
            var queryStrings = mutableMapOf<String,String>()
            for (qs in ZStr.Split(parameters, sep = "&")) {
                val comps = ZStr.Split(qs, sep = "=")
                if (comps.size == 2) {
                    val key = comps[0]
                    var value = comps[1]
                    value = ZStr.Replace(value, find = "+", with = " ")
                    value = ZStr.UrlDecode(value) ?: ""
                    queryStrings.put(key, value)
                }
            }
            return queryStrings
        }
    }
}

//fun ==(lhs: ZUrl, rhs: ZUrl) : Boolean =
//        lhs.url == rhs.url
