//
//  ZUrlAndroid.swift
//
//  Created by Tor Langballe on /30/10/15.
//

package com.github.torlangballe.cetrusandroid

import java.net.URL
//import java.nio.file.Files.isDirectory
import java.io.File
import android.content.Intent
import android.net.Uri
import java.net.MalformedURLException
import java.nio.file.Paths

open class ZUrl {
    var url: URL? = null

    constructor() {
        url = null
    }

    constructor(string: String) {
        try {
            url = URL(string)
        } catch (e: MalformedURLException) {
            url = null
        }
    }

    constructor(nativeUrl:URL) {
        url = nativeUrl
    }

    constructor(url: ZUrl) {
        this.url = url.url
    }

    val IsEmpty: Boolean
        get() = (url == null)

    fun IsDirectory() : Boolean {
        val protocol = url?.getProtocol() ?: false
        if (protocol == "file") {
            return File(url?.getFile()).isDirectory() ?: false
        }
        // TODO: Can be online web dir too?
        return false
    }

    fun OpenInBrowser(inApp: Boolean, notInAppForSocial: Boolean = true) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
        zMainActivityContext!!.startActivity(browserIntent)
    }

    fun GetName() : String {
        if (url != null) {
            val uri = Uri.parse(url!!.toString())
            return Paths.get(uri.getPath()).getFileName().toString()
        }
        return ""
    }
    val Scheme: String
        get() {
            return url?.protocol ?: ""
        }
    val Host: String
        get() {
            return url?.host ?: ""
        }
    val AbsString: String
        get() {
            return url?.toString() ?: ""
        }
    val ResourcePath: String
        get() {
            return url?.path ?: ""
        }
    var Extension: String
        get() {
            // TODO: Fix quick and dirty
            val h = ZStr.HeadUntil(AbsString, "?")
            return ZStr.TailUntil(h, "")
        }
        set(newValue) {
            ZNOTIMPLEMENTED()
        }
    val Anchor: String
        get() = url?.ref ?: ""

    val Parameters: Map<String, String>
        get() {
            val q = url?.query
            if (q != null) {
                return ParametersFromString(q)
            }
            val tail = ZStr.TailUntil((url?.toString())
                    ?: "", sep = "?")
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
