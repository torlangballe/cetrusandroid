
//
//  ZShapeView.swift
//
//  Created by Tor Langballe on /22/10/15.
//
package com.github.torlangballe.cetrusandroid

open class ZShapeView: ZContainerView, ZImageLoader {
    enum class ShapeType(val rawValue: String) {
        circle("circle"), rectangle("rectange"), roundRect("roundrect"), star("star"), none("");
        companion object : ZEnumCompanion<String, ShapeType>(ShapeType.values().associateBy(ShapeType::rawValue))
    }
    var type = ShapeType.circle
    var strokeWidth: Double = 0.0
    var text: ZTextDraw
    var image: ZImage? = null
    var imageMargin = ZSize(4.0, 1.0)
    var textXMargin = 0.0
    var imageFill = false
    var imageOpacity: Float = 1f
    var ratio = 0.3
    var count = 5
    var strokeColor = ZColor.White()
    var maxWidth: Double = 0.0
    var imageAlign = ZAlignment.Center
    var fillBox = false
    var roundImage = false
    var value: Float = 0f

    constructor(type: ShapeType, minSize: ZSize) : super(name = "ZShapeView") {

        text = ZTextDraw()
        this.minSize = minSize
        this.type = type
        foregroundColor = ZColor()
        if (type == ZShapeView.ShapeType.roundRect) {
            ratio = 0.49
        }
        if (type == ZShapeView.ShapeType.star) {
            ratio = 0.6
        }
        isAccessibilityElement = true
    }

    override fun CalculateSize(total: ZSize) : ZSize {
        var s = minSize
        if (!text.text.isEmpty()) {
            var ts = (text.GetBounds().size + ZSize(16.0, 6.0))
            ts.w = ts.w * 1.1
            // some strange bug in android doesn't allow *= here...
            s.Maximize(ts)
        }
        if (maxWidth != 0.0) {
            s.w = minOf(s.w, maxWidth)
        }
        if (type == ZShapeView.ShapeType.circle) {
            s.h = maxOf(s.h, s.w)
        }
        return s
    }

    override     fun SetImage(image: ZImage?, downloadUrl: String) {
        this.image = image
        Expose()
    }

    override fun DrawInRect(rect: ZRect, canvas: ZCanvas) {
        val path = ZPath()
        var r = LocalRect
        if (type == ZShapeView.ShapeType.roundRect) {
            r = r.Expanded(ZSize(-1.0, -1.0))
        }
        when (type) {
            ZShapeView.ShapeType.star -> path.AddStar(rect = r, points = count, inRatio = ratio)
            ZShapeView.ShapeType.circle -> path.ArcDegFromCenter(r.Center, radius = r.size.w / 2.0 - strokeWidth / 2.0)
            ZShapeView.ShapeType.roundRect -> {
                var corner = minOf(r.size.w, r.size.h) * ratio
                corner = minOf(corner, 15.0)
                path.AddRect(r, corner = ZSize(corner, corner))
            }
            ZShapeView.ShapeType.rectangle -> path.AddRect(r)
        }
        if (!foregroundColor.undefined) {
            var o = foregroundColor.Opacity
            if (!Usable) {
                o *= 0.6
            }
            canvas.SetColor(getStateColor(foregroundColor), opacity = o)
            canvas.FillPath(path)
        }
        if (strokeWidth != 0.0) {
            var o = strokeColor.Opacity
            if (!Usable) {
                o *= 0.6
            }
            canvas.SetColor(getStateColor(strokeColor), opacity = o)
            canvas.StrokePath(path, width = strokeWidth)
        }
        var imarg = imageMargin
        if (ZIsTVBox()) {
            imarg.Maximize(ZSize(7.0, 7.0))
        }
        if ((image != null)) {
            var drawImage = image
            if (isHighlighted) {
                drawImage = drawImage!!.TintedWithColor(ZColor(white = 0.2))
            }
            var o = imageOpacity
            if (!Usable) {
                o *= 0.6.toFloat()
            }
            if (imageFill) {
                canvas.PushState()
                canvas.ClipPath(path)
                canvas.DrawImage(drawImage!!, destRect = r, opacity = o)
                canvas.PopState()
            } else {
                var a = imageAlign or ZAlignment.Shrink
                if (fillBox) {
                    a = ZAlignment.None
                }
                var corner: Double? = null
                if (roundImage) {
                    if (type == ZShapeView.ShapeType.roundRect) {
                        corner = minOf(15.0, minOf(r.size.w, r.size.h) * ratio) - imarg.Min()
                    } else if (type == ZShapeView.ShapeType.circle) {
                        corner = image!!.Size.Max()
                    }
                }
                canvas.DrawImage(drawImage!!, destRect = r, align = a, opacity = o, corner = corner, margin = imarg)
            }
        }
        if ((text.text != "")) {
            var t = text.copy()
            t.color = getStateColor(t.color)
            t.rect = r.Expanded(-(strokeWidth + 2.0)).Expanded(ZSize(-textXMargin, 0.0))
            t.rect.pos.y += 3.5 * ZScreen.SoftScale
            if (imageFill) {
                canvas.SetDropShadow(ZSize(0.0, 0.0), blur = 2f)
            }
            t.Draw(canvas)
            if (imageFill) {
                canvas.SetDropShadowOff()
            }
        }
        if (isFocused) {
            ZFocus.Draw(canvas, rect = rect, corner = 9.0)
        }
    }
    override var accessibilityLabel: String?
        get() {
            if (super.accessibilityLabel != null && !super.accessibilityLabel!!.isEmpty()) {
                return super.accessibilityLabel!!
            }
            return text.text
        }
        set(newValue) {
            super.accessibilityLabel = newValue
        }
}
