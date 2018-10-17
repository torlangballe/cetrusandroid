//
//  ZTextDraw.swift
//
//  Created by Tor Langballe on /22/10/15.
//

package com.github.torlangballe.cetrusandroid

import android.graphics.Paint
import android.text.StaticLayout
import android.text.TextPaint
import kotlin.math.ceil

enum class ZTextDrawType { fill, stroke, clip }

enum class ZTextWrapType {
    default,
    word,
    char,
    clip,
    headTruncate,
    tailTruncate,
    middleTruncate
}

data class ZTextDraw(
        var type: ZTextDrawType = ZTextDrawType.fill,
        var wrap: ZTextWrapType = ZTextWrapType.default,
        var text:String = "",
        var color: ZColor = ZColor.Black(),
        var alignment: ZAlignment = ZAlignment.Center,
        var font: ZFont = ZFont("Helvetica", 18.0),
        var rect: ZRect = ZRect(),
        var pos: ZPos? = null,
        var lineSpacing: Float = 0f,
        var strokeWidth: Float = 1f,
        var maxLines: Int = 0) {
    //        let size = NSString(string: text).sizeWithAttributes(MakeAttributes())
    companion object {

//        fun GetNativeWrapMode(w: ZTextWrapType) : NSLineBreakMode {
//            when (w) {
//                WrapType.word -> return NSLineBreakMode.byWordWrapping
//                WrapType.char -> return NSLineBreakMode.byCharWrapping
//                WrapType.headTruncate -> return NSLineBreakMode.byTruncatingHead
//                WrapType.tailTruncate -> return NSLineBreakMode.byTruncatingTail
//                WrapType.middleTruncate -> return NSLineBreakMode.byTruncatingMiddle
//                else -> return NSLineBreakMode.byClipping
//            }
//        }

//        fun GetTextAdjustment(style: ZAlignment) : NSTextAlignment {
//            if ((style and ZAlignment.Left)) {
//                return NSTextAlignment.left
//            } else if ((style and ZAlignment.Right)) {
//                return NSTextAlignment.right
//            } else if ((style and ZAlignment.HorCenter)) {
//                return NSTextAlignment.center
//            } else if ((style and ZAlignment.HorJustify)) {
//                exit(-1)
//            }
//            return NSTextAlignment.left
//        }
    }

    fun makeTextPaint() : TextPaint {
        val textPaint = TextPaint()
        textPaint.typeface = font.typeface
        textPaint.isAntiAlias = true
        textPaint.textSize  = font.size.toFloat() * ZScreen.Scale.toFloat()
        textPaint.color = color.color.toArgb()
        when (this.type) {
            ZTextDrawType.stroke -> {
                    textPaint.style = Paint.Style.STROKE
                    textPaint.strokeWidth = strokeWidth
                }
            else -> textPaint.style = Paint.Style.FILL
        }
///            ZTextDrawType.clip -> canvas.context.setTextDrawingMode(CGTextDrawingMode.clip)

        return textPaint
    }

    fun calculateSize() : ZSize {
        var textPaint = makeTextPaint()
        val builder = StaticLayout.Builder.obtain (text, 0, text.length, textPaint, (rect.size.w * ZScreen.Scale).toInt())
        val slayout = builder.build()

        val width = textPaint.measureText(text)
        val height = -textPaint.ascent() + textPaint.descent()

        var size = ZSize(slayout.width.toDouble(), slayout.height.toDouble())

        if (width < size.w) {
            return ZSize(width, height)
        }
        return size
    }

    fun GetBounds(noWidth: Boolean = false) : ZRect {
        if (rect.IsNull) {
            val textPaint = makeTextPaint()
            val width = textPaint.measureText(text)
            val height = -textPaint.ascent() + textPaint.descent()
            var s = ZSize(width.toDouble(), height.toDouble())
            s /= ZScreen.Scale
            return ZRect(size = s)
        }
        var size =  calculateSize()

        // TODO: make
        // https@ //stackoverflow.com/questions/41779934/how-is-staticlayout-used-in-android
        // https://developer.android.com/reference/android/text/StaticLayout.Builder.html#build()
        // https://stackoverflow.com/questions/11120392/android-center-text-on-canvas


        size /= ZScreen.Scale
        if (maxLines > 1) {
            size.h = (font.lineHeight).toDouble() * maxLines.toDouble()
        }
        return rect.Align(size, align = alignment)
    }

//    fun MakeAttributes() : Map<NSAttributedStringKey, Any> {
//        val pstyle = NSParagraphStyle.default.mutableCopy() as! NSMutableParagraphStyle
//                pstyle.lineBreakMode = ZTextDraw.GetNativeWrapMode(wrap)
//        pstyle.alignment = ZTextDraw.GetTextAdjustment(alignment)
//        //        pstyle.allowsDefaultTighteningForTruncation = true
//        if (lineSpacing != 0.0) {
//            pstyle.maximumLineHeight = font.lineHeight + CGFloat(lineSpacing)
//            pstyle.lineSpacing = CGFloat(lineSpacing)
//            // CGFloat(max(0.0, lineSpacing))
//        }
//        return mapOf(NSAttributedStringKey.font to font, NSAttributedStringKey.paragraphStyle to pstyle, NSAttributedStringKey.foregroundColor to color.color)
//    }

    fun Draw(canvas: ZCanvas) : ZRect {
        if (text.isEmpty()) {
            return ZRect(pos = rect.pos, size = ZSize(0, 0))
        }
        //!        let attributes = MakeAttributes()
        var ts = GetBounds().size
        val p: ZPos
        if (pos == null) {
            val r = rect
            ts = ZSize(ceil(ts.w), ceil(ts.h))
            val ra = rect.Align(ts, align = alignment)
            if ((alignment and ZAlignment.Top)) {
                r.Max.y = ra.Max.y
            } else if ((alignment and ZAlignment.Bottom)) {
                r.pos.y = r.Max.y - ra.size.h
            } else {
                r.pos.y = ra.pos.y - (font.lineHeight).toDouble() * 0.3
            }
            p = ZPos(r.Min.x, r.Min.y + font.size * 1.1)
            if (alignment and ZAlignment.Right) {
                p.x = r.Max.y
            } else if (alignment and ZAlignment.HorCenter) {
                p.x = r.Center.x
            }
        } else {
            p = pos!!
        }
        if ((alignment and ZAlignment.HorShrink)) {//         ScaleFontToFit()
        }
        canvas.DrawText(text, font, color, p, alignment)
        return rect.Align(ts, align = alignment)
    }

    fun ScaleFontToFit(minScale: Double = 0.5) {
        val w = rect.size.w * 0.95
        val s = GetBounds(noWidth = true).size
        if (s.w > w) {
            var r = w / s.w
            if (r < 0.94) {
                r = maxOf(r, minScale)
                font = font.NewWithSize(font.size * r)!!
            }
        } else if (s.h > rect.size.h) {
            val r = maxOf(5.0, (rect.size.h / s.h) * 1.01)
//            font = ZFont(name = font.fontName, (font.pointSize).toDouble() * r)!!
        }
    }

    /*
    fun CreateLayer(margin: ZRect = ZRect()) : ZTextLayer {
        val textLayer = ZTextLayer()
        textLayer.font = font
        textLayer.fontSize = font.pointSize
        textLayer.string = text
        textLayer.contentsScale = CGFloat(ZScreen.Scale)
        if (alignment and .HorCenter) {
            textLayer.alignmentMode = kCAAlignmentCenter
        }
        if (alignment and .Left) {
            textLayer.alignmentMode = kCAAlignmentLeft
        }
        if (alignment and .Right) {
            textLayer.alignmentMode = kCAAlignmentRight
        }
        textLayer.foregroundColor = color.color.cgColor
        val s = (GetBounds().size + margin.size)
        textLayer.frame = ZRect(size = s).GetCGRect()
        return textLayer
    }
    */
}
