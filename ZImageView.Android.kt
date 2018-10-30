//  ZImageView.swift
//  Zed
//
//  Created by Tor Langballe on /20/10/15.
//  Copyright © 2015 Capsule.fm. All rights reserved.
//

package com.github.torlangballe.cetrusandroid

interface ZImageLoader {
    fun SetImage(image: ZImage?, downloadUrl:String)
}

class ZImageView: ZCustomView, ZImageLoader {
    // use contentMode for aspect fill etc
    override var isHighlighted: Boolean = false
    override var objectName: String
    override var Usable: Boolean = true // for now

    var image: ZImage? = null
    var maxSize = ZSize()
    var margin = ZSize()
    var hightlightTint = ZColor(white = 0.4)
    var downloadUrl = ""
    var edited = false
    var minUrlImageSize = ZSize()
    // if not null, and downloaded image is < w and h of this, dont show
    var alignment = ZAlignment.None

    var HandlePressedInPosFunc : ((pos: ZPos) -> Unit)?
        get() {
            return touchInfo.handlePressedInPosFunc
        }
        set(h) {
            touchInfo.handlePressedInPosFunc = h
            isAccessibilityElement = true
            //        accessibilityTraits |= UIAccessibilityTraitButton
        }

    constructor(zimage: ZImage? = null, name: String = "ZImageView", maxSize: ZSize = ZSize()) : super(name) {
        objectName = name
        this.maxSize = maxSize
        // this.contentMode = .scaleAspectFit
        isAccessibilityElement = true
        SetImage(zimage, "")
    }

    constructor(namedImage: String, scaleInsets: ZRect = ZRect.Null, maxSize: ZSize = ZSize()) : super(namedImage!!) {
        objectName = namedImage
        this.maxSize = maxSize
        var image = ZImage(named = namedImage)
        if (!scaleInsets.IsNull) {
            image = image.Make9PatchImage(capInsets = scaleInsets)
        }
//            this.contentMode = UIViewContentMode.scaleAspectFit
        SetImage(image, "")
    }

    constructor(url: String, maxSize: ZSize = ZSize(), downloaded: ((success: Boolean) -> Unit)? = null) : this(zimage = null, name = url, maxSize = maxSize) {
        downloadUrl = url
        if (!url.isEmpty()) {
            this.DownloadFromUrl(url) {
                { success: Boolean -> }
            }
        }
    }

    override fun DrawInRect(rect: ZRect, canvas: ZCanvas) {
        super.DrawInRect(rect, canvas)
//        canvas.SetColor(ZColor.Green())
//        canvas.FillPath(ZPath(rect = rect))

        if (image != null) {
            var drawImage = image!!
            if (isHighlighted) {
                drawImage = drawImage.TintedWithColor(ZColor(white = 0.5))
            }
            val r = LocalRect.Align(drawImage.Size, ZAlignment.Center or ZAlignment.Shrink)
            canvas.DrawImage(drawImage, destRect = r)
        }
    }

    override fun View() : UIView {
        return this
    }

    override fun CalculateSize(total: ZSize): ZSize {
        var s = minSize
        if (image != null) {
            s = image!!.Size
        }
        if (!maxSize.IsNull()) {
            s = ZRect(size = maxSize).Align(s, align = ZAlignment.Center or ZAlignment.Shrink).size
        }
        return s
    }

    override fun SetImage(image: ZImage?, downloadUrl: String) {
        this.downloadUrl = downloadUrl
        this.image = image
        Expose()
    }

//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        return handleTouch(this, event, touchInfo)
//    }
//
    override fun AddTarget(t: ZCustomView?, forEventType: ZControlEventType) {
        touchInfo.tapTarget = t
        assert(forEventType == ZControlEventType.pressed)
//        isUserInteractionEnabled = true
        isAccessibilityElement = true
//        accessibilityTraits |= UIAccessibilityTraitButton
//        if (highlightedImage == null && image != null) {
//            highlightedImage = image!!.TintedWithColor(hightlightTint)
//        }
    }

    fun SetAnimatedImages(images: List<ZImage>, durationForAll: Float, start: Boolean = true) {
        ZNOTIMPLEMENTED()
    }

    fun Animate(on: Boolean) {
        ZNOTIMPLEMENTED()
    }

    fun SetAnimatedImagesFromWildcard(wildcard: String, durationForAll: Float, start: Boolean = true) {
        val images = ZImage.GetNamedImagesFromWildcard(wildcard)
        if (images.size > 0) {
            SetAnimatedImages(images, durationForAll = durationForAll, start = start)
        } else {
            SetImage(null, "")
        }
    }
}

fun ZImageLoader.DownloadFromUrl(url: String, cache: Boolean = true, done: ((success: Boolean) -> Unit)? = null) {
    val s = this
    ZImage.DownloadFromUrl(url, cache = cache) { image ->
        if (image != null) {
            s.SetImage(image, downloadUrl = url)
            done?.invoke(true)
        } else {
            done?.invoke(false)
        }
    }
}

