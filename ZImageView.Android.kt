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
            val r = LocalRect.Align(image!!.Size, ZAlignment.Left)
            canvas.DrawImage(image!!, destRect = r)
        }
    }

//    override fun draw(canvas: Canvas?) {
//        val paint = Paint()
//        paint.color = Color.GREEN
//        paint.style = Paint.Style.FILL
//        val path = Path()
//        val r = ZRectToAndroidRectF(LocalRect)
//        path.addRect(r, Path.Direction.CW)
//        canvas!!.drawPath(path, paint)
//        super.draw(canvas)
//    }

    override fun View() : UIView {
        return this
    }

    /*
    //override fun layoutSubviews() {
        //        if _isDebugAssertConfiguration() {
        //            if accessibilityLabel == nil && isAccessibilityElement { // isAccessibilityElement is BOOL, not Boolean
        //                //!                print("ZImageView: No accessiblity label")
        //            }
        //        }
        if (handlePressedInPosFunc != null) {
//            isUserInteractionEnabled = true
//            if (highlightedImage == null && image != null) {
//                highlightedImage = image!!.TintedWithColor(hightlightTint)
//            }
        }
    }
*/
    /*
override fun touchesBegan(touches: Set<UITouch>, event: UIEvent?) {
        if (tapTarget != null) {
            val pos = ZPos(touches.firstOrNull()!!.location(in = this))
            tapTarget?.HandleTouched(this, state = .began, pos = pos, inside = true)
            if (touchDownRepeatSecs != 0) {
                touchDownRepeats = 0
                touchDownRepeatTimer.Set(touchDownRepeatSecs, owner = this) {   ->
                    if (this.touchDownRepeats > 2) {
                        this.tapTarget!!.HandlePressed(this!!, pos = pos)
                    }
                    this.touchDownRepeats += 1
                    true
                }
            }
        }
        isHighlighted = true
        Expose()
    }
}

override fun touchesEnded(touches: Set<UITouch>, event: UIEvent?) {
    if (isUserInteractionEnabled) {
        var handled = false
        isHighlighted = false
        PerformAfterDelay(0.05) {   ->
            this.Expose()
        }
        if (tapTarget != null || handlePressedInPosFunc != null) {
            val pos = ZPos(touches.firstOrNull()!!.location(in = this))
            val inside = LocalRect.Contains(pos)
            if (tapTarget != null) {
                handled = tapTarget!!.HandleTouched(this, state = .ended, pos = pos, inside = inside)
            }
            if (inside && !handled) {
                if (handlePressedInPosFunc != null) {
                    handlePressedInPosFunc!!.invoke(pos)
                } else {
                    tapTarget?.HandlePressed(this, pos = ZPos(touches.firstOrNull()!!.location(in = this)))
                }
            }
            touchDownRepeatTimer.Stop()
        }
        if (animationImages != null) {
            startAnimating()
        }
    }
}

override fun touchesCancelled(touches: Set<UITouch>, event: UIEvent?) {
    if (isUserInteractionEnabled) {
        if (tapTarget != null) {
            tapTarget?.HandleTouched(this, state = .canceled, pos = ZPos(), inside = false)
        }
        isHighlighted = false
        Expose()
        touchDownRepeatTimer.Stop()
        if (animationImages != null) {
            startAnimating()
        }
    }
}
*/

    /*
override fun sizeThatFits(size: CGSize) : CGSize {
    if (!maxSize.IsNull()) {
        return maxSize.GetCGSize()
    }
    val s = ZSize(super.sizeThatFits(size))
    return (s + margin * 2.0).GetCGSize()
}
*/

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var s = ZSize(10, 10)
        if (image != null) {
            s = image!!.Size
        }
        s *= ZScreen.Scale
        setMeasuredDimension(s.w.toInt(), s.h.toInt())
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

