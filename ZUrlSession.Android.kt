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
import java.net.*

class ZUrlRequest {
    var url: URL? = null
    var httpMethod: String = "GET"
    var timeoutIntervalSecs: Int? = null
    var headers = mutableMapOf<String, String>()
    var httpBody = byteArrayOf(0)

    fun SetUrl(url: String) {
        try {
            val u = URL(url);
            this.url = u
        } catch (e:java.lang.Exception) {
            ZDebug.Print("ZUrlRequest SetUrl err:", e.localizedMessage)
        }
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

fun doDone(onMain: Boolean, data: ZData?, err: String?, done: (response: ZUrlResponse?, data: ZData?, error: ZError?) -> Unit) {
    var error: ZError? = null
    if (err != null) {
        error = ZNewError(err)
    }
    if (!onMain) {
        done(ZUrlResponse(), data, error)
    } else {
        ZMainQue.sync {
            done(ZUrlResponse(), data, error)
        }
    }
}

class ZUrlSession {
    // transactions are debugging list for listing all transactions
    companion object {
        fun Send(request: ZUrlRequest, onMain: Boolean = true, async: Boolean = true, makeStatusCodeError: Boolean = false, done: (response: ZUrlResponse?, data: ZData?, error: ZError?) -> Unit) : ZURLSessionTask? {
            if (!async) {
                SendSync(request, makeStatusCodeError = makeStatusCodeError, done = done)
                return null
            }
            ZGetBackgroundQue().async {
                var urlConnection: HttpURLConnection? = null
                try {
                    urlConnection = request.url!!.openConnection() as HttpURLConnection
                    urlConnection.requestMethod = request.httpMethod
                    val bin = BufferedInputStream(urlConnection.inputStream)
                    val ba = bin.readBytes()
                    val data = ZData(data = ba)
//                    bin.read(data.data)
                    doDone(onMain, data, null, done)
                } catch (ex: MalformedURLException) {
                    doDone(onMain, null, "MalformedURLException: " + ex.localizedMessage, done)
                } catch (ex: IOException) {
                    doDone(onMain, null, "IOException: " + ex.localizedMessage, done)
                } catch (ex: Exception) {
                    doDone(onMain, null, "Exception: " + ex.localizedMessage, done)
                } finally {
                    urlConnection?.disconnect()
                }
            }
            return ZURLSessionTask()
        }

        fun DownloadPersistantlyToFileInThread(request: ZUrlRequest, onCellular: Boolean? = null, makeStatusCodeError: Boolean = false, done: (response: ZUrlResponse?, file: ZFileUrl?, error: ZError?) -> Unit) : ZURLSessionTask? {
            ZNOTIMPLEMENTED()
            return ZURLSessionTask()
        }

        fun SendSync(request: ZUrlRequest, timeoutSecs: Double = 11.0, makeStatusCodeError: Boolean = false, done: (response: ZUrlResponse?, data: ZData?, error: ZError?) -> Unit) {
            ZNOTIMPLEMENTED()
        }

        fun GetAllCookies() : List<String> {
            ZNOTIMPLEMENTED()
            return listOf<String>()
        }

        fun DeleteAllCookiesForDomain(domain: String) {
            ZNOTIMPLEMENTED()
        }
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

