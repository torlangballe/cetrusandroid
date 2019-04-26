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
import java.util.concurrent.CountDownLatch

class ZUrlRequest {
    var url: URL? = null
    var httpMethod: String = "GET"
    var timeoutIntervalSecs: Int? = null
    var headers = mutableMapOf<String, String>()
    var httpBody:ZData? = null

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

            val query = args.stringFromHttpParameters()
            if (query.isNotEmpty()) {
                vurl += "?" + query
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
    var StatusCode:Int = 0
    var ContentLength:Int = 0
    var headers: Map<String, String> = mapOf()
    var debugUrl: String = ""
    fun GetSimpleStringHeaders() : Map<String, String> {
        return headers
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

fun doDone(con: HttpURLConnection?, onMain: Boolean, data: ZData?, err: String?, done: (response: ZUrlResponse?, data: ZData?, error: ZError?) -> Unit) {
    var error: ZError? = null
    if (err != null) {
        error = ZNewError(err)
    }
    var response = ZUrlResponse()
    if (con != null) {
        response.debugUrl = con.url.path
        try {
            response.StatusCode = con.responseCode // this can throw error if not "connected"
        } catch (e:Exception) { }
        var m: MutableMap<String, String> = mutableMapOf()
        try {
            for ((k, v) in con!!.headerFields) {
                if (k != null) {
                    m.set(k, v.first())
                }
            }
        } catch (e:Exception) { }
        response.headers = m
    }
    if (!onMain) {
        done(response, data, error)
    } else {
        ZMainQue.sync {
            done(response, data, error)
        }
    }
}

class ZUrlSession {
    // transactions are debugging list for listing all transactions
    companion object {

        fun send(request: ZUrlRequest, onMain: Boolean = true, makeStatusCodeError: Boolean = false, done: (response: ZUrlResponse?, data: ZData?, error: ZError?) -> Unit) : ZURLSessionTask? {
            var urlConnection: HttpURLConnection? = null
            var data: ZData? = null
            try {
                urlConnection = request.url!!.openConnection() as HttpURLConnection
                urlConnection.requestMethod = request.httpMethod
                if (request.httpBody != null) {
                    val os = urlConnection.getOutputStream()
                    os.write(request.httpBody!!.data)
                    os.close()
                }
                val bin = BufferedInputStream(urlConnection.inputStream)
                val ba = bin.readBytes()
                data = ZData(data = ba)
//                    bin.read(data.data)
                doDone(urlConnection, onMain, data, null, done)
            } catch (ex: MalformedURLException) {
                doDone(urlConnection, onMain, data, "MalformedURLException: " + ex.localizedMessage, done)
            } catch (ex: IOException) {
                doDone(urlConnection, onMain, data, "IOException: " + ex.localizedMessage, done)
            } catch (ex: Exception) {
                doDone(urlConnection, onMain, data, "Exception: " + ex.localizedMessage, done)
            } finally {
                urlConnection?.disconnect()
            }
            return ZURLSessionTask()
        }

        fun Send(request: ZUrlRequest, onMain: Boolean = true, async: Boolean = true, makeStatusCodeError: Boolean = false, done: (response: ZUrlResponse?, data: ZData?, error: ZError?) -> Unit) : ZURLSessionTask? {
            if (async) {
                ZGetBackgroundQue().async {
                    send(request, onMain, makeStatusCodeError, done)
                }
            } else {
                send(request, onMain, makeStatusCodeError, done)
            }
            return ZURLSessionTask()
        }

        fun SendSync(request: ZUrlRequest, timeoutSecs: Double = 11.0, makeStatusCodeError: Boolean = false, done: (response: ZUrlResponse?, data: ZData?, error: ZError?) -> Unit) {
            Send(request, onMain = false, async = false, makeStatusCodeError =  makeStatusCodeError, done = done)
        }

        fun DownloadPersistantlyToFileInThread(request: ZUrlRequest, onCellular: Boolean? = null, makeStatusCodeError: Boolean = false, done: (response: ZUrlResponse?, file: ZFileUrl?, error: ZError?) -> Unit) : ZURLSessionTask? {
            ZNOTIMPLEMENTED()
            return ZURLSessionTask()
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

