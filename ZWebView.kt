//
//  ZWebView.swift
//  Zed
//
//  Created by Tor Langballe on /7/12/15.
//  Copyright © 2015 Capsule.fm. All rights reserved.
//

package com.github.torlangballe.cetrusandroid

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.webkit.*

interface ZWebViewDelegate {
    fun HandleFinishedDownload(view: ZWebView, url: String) {}
    fun HandleError(view: ZWebView, error: ZError) {}
    fun HandleShouldNotLoadUrl(view: ZWebView, url: String) : Boolean { return false }
    fun HandleEnableBack(view: ZWebView, enabled: Boolean) {}
    fun HandleEnableForward(view: ZWebView, enabled: Boolean) {}
}

class ZWebView: WebView, ZView {
    var content = ""
    var currentUrl: String
    var isInFullScreenPlayback = false
    var useCookies = false
    var zdelegate: ZWebViewDelegate? = null
    //var *wprogress = : ZActivityWgt     ;         // links to external progress
    var backButton: ZShapeView? = null
    var forwardButton: ZShapeView? = null
    var calculateSize = false
    var maxSize: ZSize
    var minSize: ZSize
    
    override fun View() : View = this
    override var objectName = "ZWebView"
    override var isHighlighted = false
    override var Usable = true

    //    var scrollviewTransparent = false             ;
    //    var mobileizeURLs = true
    var makeUserAgentDesktopBrowser = ZDevice.IsIPad
    
    constructor(url: String = "", minSize: ZSize, scale: Boolean = true, content: String = "") : super(zGetCurrentContext()!!) {
        currentUrl = url
        this.minSize = minSize
        this.maxSize = minSize

        getSettings().setJavaScriptEnabled(true)
        minimumWidth = minSize.w.toInt()
        minimumHeight = minSize.h.toInt()
        zLayoutViewAndScale(this, ZRect(0.0, 0.0, minSize.w, minSize.h))

        this.webViewClient =  (object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            }
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                ZDebug.Print("web error:", error, request.url)
            }

            override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
                ZDebug.Print("web http-error:", errorResponse, request.url)
            }

            override fun onPageFinished(view: WebView, url: String) {
                webViewDidFinishLoad(url)
            }
        })
        ZPerformAfterDelay(0.1) {
            if (!content.isEmpty()) {
                LoadContent(content, baseUrl = currentUrl)
            } else if (!currentUrl.isEmpty()) {
                LoadURL(currentUrl)
            }
        }
    }

    fun Clear() {
        LoadURL("about:blank")
    }
    
    fun GetTextInHTML() : String =
        EvaluateJavascriptToString("document.documentElement.innerText;")

    fun GetImage() : ZImage? {
        val s = Rect.size
        val bm = Bitmap.createBitmap(s.w.toInt(), s.h.toInt(), Bitmap.Config.ARGB_8888)

        val bigcanvas = Canvas(bm)
        val paint = Paint()
        val iHeight = bm.getHeight()
        bigcanvas.drawBitmap(bm, 0f, iHeight.toFloat(), paint)
        draw(bigcanvas)

        val image = ZImage()
        image.bitmap = bm

        return image
    }

    fun webViewDidFinishLoad(url: String) {
        var vurl = url
        if (vurl.isEmpty()) {
            vurl = EvaluateJavascriptToString("window.location")
        }
        this.currentUrl = vurl
//        if (calculateSize) {
//            val height = this.sizeThatFits(CGSize.zero).height
//            minSize.h = minOf(maxSize.h, height.toDouble())
//        }
        zdelegate?.HandleFinishedDownload(this, url = currentUrl)
    }

    /*
    fun copyCookies(request: NSMutableURLRequest) {
        val cookies = HTTPCookieStorage.shared.cookies
        if (cookies != null) {
            request.httpShouldHandleCookies = true
            if (cookies.size > 0) {
                var dict = mutableMapOf<String, String>()
                for (cookie in cookies) {
                    dict[cookie.name] = cookie.value
                }
                val header = dict.stringFromHttpParameters(escape = false)
                request.setValue(header, forHTTPHeaderField = "Cookie")
            }
        }
    }
    */

    fun LoadURL(url: String) {
        currentUrl = url
        content = ""
        loadUrl(url)
//        if (makeUserAgentDesktopBrowser) {
//            nsRequest.setValue("%s Safari/528.16", forHTTPHeaderField = "User_Agent")
//        }
//        if (useCookies) {
//            copyCookies(nsRequest)
//        }
//        this.loadRequest(nsRequest as URLRequest)
    //    if(wprogress)
    //        wprogress->Show(true);
    }
    
    fun LoadContent(content: String, baseUrl: String, isJavaScriptCommand: Boolean = false) {
        this.content = content
        currentUrl = baseUrl
        if (isJavaScriptCommand) {
            EvaluateJavascriptToString(content)
        } else {
            loadDataWithBaseURL(null, content, "text/html", "UTF-8", null)
        }
    }
    
    fun IsLoading() = false
    
    //self.IsLoading() infinite call?
    fun Stop() {
        this.stopLoading()
    }
    val CanGoBack: Boolean
        get() = false
    val CanGoForward: Boolean
        get() = false
    
    fun GoBack() {
    }
    
    fun GoForward() {
    }
    val EstimatedProgress: Float
        get() = 0.5f
    
    //mac:[ WEBVIEW estimatedProgress ];
    fun EvaluateJavascriptToString(java: String) : String {
        loadUrl("javascript:" + java);
        return "";
    }
}

fun ZWebViewDelegate.HandleShouldNotLoadUrl(view: ZWebView, url: String) : Boolean =
    false

fun ZWebViewDelegate.HandleEnableBack(view: ZWebView, enabled: Boolean) {}

fun ZWebViewDelegate.HandleEnableForward(view: ZWebView, enabled: Boolean) {}
