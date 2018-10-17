//
//  ZURLConnection.swift
//  Zed
//
//  Created by Tor Langballe on /5/11/15.
//  Copyright © 2015 Capsule.fm. All rights reserved.
//

package com.github.torlangballe.cetrusandroid

import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class ZUrlRequest {
    var url: URL? = null
    var httpMethod: String = "GET"
    var timeoutIntervalSecs: Int? = null
    var headers = mutableMapOf<String, String>()
    var httpBody = byteArrayOf(0)

    fun SetUrl(url: String) {
        val u = URL(url);
        this.url = u
    }

    fun SetType(type: ZUrlRequestType) {
        httpMethod = type.rawValue
    }

    fun SetHeaderForKey(key: String, value: String) {
        headers.put(key, value)
    }

    companion object {
        fun Make(type: ZUrlRequestType, url: String, timeOutSecs: Int = 0, args: Map<String, String> = mapOf()) : ZUrlRequest {
            val req = ZUrlRequest()
            var vurl = url

            var query = ""
            for (a in args) {
                if (!query.isEmpty()) {
                    query += ","
                }
                query += ZStr.UrlEncode(a.key) + "=" + ZStr.UrlEncode(a.value)
            }
            req.SetUrl(vurl)
            if (timeOutSecs != 0) {
                req.timeoutIntervalSecs = timeOutSecs
            }
            req.SetType(type)
            return req
        }
    }
}

class ZUrlResponse {
    val StatusCode:Int = 0
    val ContentLength:Int = 0

    fun GetSimpleStringHeaders() : Map<String, String> {
        return mapOf<String, String>()
    }
}

class ZURLSessionTask {
    var FractionCompleted:Double = 0.0
}

enum class ZUrlRequestType(val rawValue: String) {
    Post("POST"), Get("GET"), Put("PUT"), Delete("DELETE");
}

data class ZUrlRequestReturnMessage(
        var messages: List<String>? = null,
        var message: String? = null,
        var code: Int? = null) {
}

/*
private fun checkStatusCode(response: ZUrlResponse, check: Boolean, ref error:  ZError?) {
    if (check) {
        if (error == null) {
            val code = response.StatusCode
            if (code != null) {
                if (code >= 300) {
                    val str = "$code")
                    error = ZNewError(str, code = code, domain = ZUrlErrorDomain)
                }
            }
        }
    }
}
*/

class ZUrlSession {
    // transactions are debugging list for listing all transactions
    companion object {
        fun Send(request: ZUrlRequest, onMain: Boolean = true, async: Boolean = true, sessionCount: Int = -1, makeStatusCodeError: Boolean = false, done: (response: ZUrlResponse?, data: ZData?, error: ZError?, sessionCount: Int) -> Unit) : ZURLSessionTask? {
            if (!async) {
                SendSync(request, sessionCount = sessionCount, makeStatusCodeError = makeStatusCodeError, done = done)
                return null
            }
            var urlConnection: HttpURLConnection? = null
            try {
                val url = URL("http://www.android.com/")
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = request.httpMethod
                val bin = BufferedInputStream(urlConnection.inputStream)
                var data = ZData()
                bin.read(data.data)
                done(ZUrlResponse(), data, null, sessionCount)
            }
            catch (ex: MalformedURLException) {
                done(ZUrlResponse(), null, ZNewError("MalformedURLException"), sessionCount)
            }
            catch (ex: IOException) {
                done(ZUrlResponse(), null, ZNewError("IOException"), sessionCount)
            }
            catch (ex: Exception) {
                done(ZUrlResponse(), null, ZNewError("Exception"), sessionCount)
            }
            finally {
                urlConnection?.disconnect()
            }
            return ZURLSessionTask()
        }

        fun DownloadPersistantlyToFileInThread(request: ZUrlRequest, onCellular: Boolean? = null, makeStatusCodeError: Boolean = false, done: (response: ZUrlResponse?, file: ZFileUrl?, error: ZError?) -> Unit) : ZURLSessionTask? {
            ZNOTIMPLEMENTED()
            return ZURLSessionTask()
        }

        fun SendSync(request: ZUrlRequest, timeoutSecs: Double = 11.0, sessionCount: Int = -1, makeStatusCodeError: Boolean = false, done: (response: ZUrlResponse?, data: ZData?, error: ZError?, sessionCount: Int) -> Unit) {
            ZNOTIMPLEMENTED()
        }

        fun GetAllCookies() : List<String> {
            ZNOTIMPLEMENTED()
            return listOf<String>()
        }

        fun DeleteAllCookiesForDomain(domain: String) {
            ZNOTIMPLEMENTED()
        }

//        fun CheckError(data: ZJSONData) : Pair<ZError?, Int?> {
//            ZNOTIMPLEMENTED()
//            return Pair(null, null)
//        }
    }
}

class ZRateLimiter {
    val max: Int
    val durationSecs: Double
    var timeStamps = mutableListOf<ZTime>()

    constructor(max: Int, durationSecs: Double) {
        this.max = max
        this.durationSecs = durationSecs
    }

    fun Add() {
        timeStamps.append(ZTime.Now())
    }

    fun IsExceded() : Boolean {
        timeStamps.removeIf { it.Since() > durationSecs }
        return timeStamps.size >= max
    }
}

