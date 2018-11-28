
//
//  ZImageView.swift
//  Zed
//
//  Created by Tor Langballe on /20/10/15.
//  Copyright © 2015 Capsule.fm. All rights reserved.
//
package com.github.torlangballe.cetrusandroid

class ZImageView: ZCustomView, ZImageLoader {
    var image: ZImage? = null
    var maxSize = ZSize()
    var margin = ZSize()
    var hightlightTint = ZColor(white = 0.4)
    var downloadUrl = ""
    var edited = false
    var minUrlImageSize = ZSize()
    // if not null, and downloaded image is < w and h of this, dont show
    var alignment = ZAlignment.None

    // to align when contentMode is fit/scale
    constructor(zimage: ZImage? = null, name: String = "ZImageView", maxSize: ZSize = ZSize()) : super(name = name) {
        objectName = name
        this.maxSize = maxSize
        image = zimage
        isAccessibilityElement = true
    }

    constructor(namedImage: String, scaleInsets: ZRect = ZRect.Null, maxSize: ZSize = ZSize()) : super(name = namedImage) {
        objectName = namedImage
        this.maxSize = maxSize
        var im = ZImage(named = namedImage)
        if (im != null) {
            if (!scaleInsets.IsNull) {
                image = im.Make9PatchImage(capInsets = scaleInsets)
            } else {
                image = im
            }
        }
    }

    constructor(url: String, maxSize: ZSize = ZSize(), downloaded: ((success: Boolean) -> Unit)? = null) : this(zimage = null, name = url, maxSize = maxSize) {
        downloadUrl = url
        if (!url.isEmpty()) {
            this.DownloadFromUrl(url) { sucess  ->  }
        }
    }

//    override fun sizeThatFits(size: CGSize) : CGSize {
//        if (!maxSize.IsNull()) {
//            return maxSize.GetCGSize()
//        }
//        val s = ZSize(super.sizeThatFits(size))
//        return (s + margin * 2.0).GetCGSize()
//    }
//
    override fun CalculateSize(total: ZSize) : ZSize {
        var s = minSize
        if (image != null) {
            s = image!!.Size
        }
        if ((!maxSize.IsNull())) {
            s = ZRect(size = maxSize).Align(s, align = ZAlignment.Center or ZAlignment.Shrink).size
        }
        return s
    }

    override fun SetImage(image: ZImage?, downloadUrl: String) {
        this.downloadUrl = downloadUrl
        this.image = image
        //        if minSize != nil && image != nil {
        //            if image!.Size < minSize! {
        //            }
        //        }
        Expose()
    }

    override fun DrawInRect(rect: ZRect, canvas: ZCanvas) {
        super.DrawInRect(rect, canvas = canvas)
        if (image != null) {
            var drawImage = image!!
            if ((isHighlighted)) {
                drawImage = drawImage.TintedWithColor(ZColor(white = 0.5))
            }
            val r = LocalRect.Align(drawImage.Size, align = ZAlignment.Center or ZAlignment.Shrink)
            canvas.DrawImage(drawImage, destRect = r)
            if (isFocused) {
                ZFocus.Draw(canvas, rect = r, corner = 8.0)
            }
        }
    }

//    override fun AddTarget(t: ZCustomView?, forEventType: ZControlEventType) {
//        touchInfo.tapTarget = t
//        assert(forEventType == ZControlEventType.pressed)
////        isUserInteractionEnabled = true
//        isAccessibilityElement = true
////        accessibilityTraits |= UIAccessibilityTraitButton
//    }
}

interface ZImageLoader {
    fun SetImage(image: ZImage?, downloadUrl: String)
}

fun ZImageLoader.DownloadFromUrl(url: String, cache: Boolean = true, done: ((success: Boolean) -> Unit)? = null) {
    // , contentMode mode: UIViewContentMode
    val s = this
    ZImage.DownloadFromUrl(url, cache = cache) { image  ->
        if (image != null) {
            s.SetImage(image, url)
            done?.invoke(true)
        } else {
            done?.invoke(false)
        }
    }
}
